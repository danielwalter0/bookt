package dev.bookt;


import dev.bookt.resource.Resource;
import dev.bookt.resource.ResourceRepository;
import dev.bookt.tenant.Tenant;
import dev.bookt.tenant.TenantRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Testcontainers
@SpringBootTest
class BookingFlowIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private TenantRepository tenantRepository;

    @Autowired
    private ResourceRepository resourceRepository;

    @Test
    void savesAndRetrievesTenantAndResource() {
        Tenant tenant = new Tenant("Test tenant", "test hash", null);
        tenant = tenantRepository.save(tenant);
        assertNotNull(tenant.getId());

        Resource resource = new Resource("Test Court", tenant, 30, LocalTime.of(8,0), LocalTime.of(22,0));
        resource = resourceRepository.save(resource);

        Resource fetched = resourceRepository.findById(resource.getId()).orElseThrow();
        assertEquals("Test Court", fetched.getName());
        assertEquals(tenant.getId(), fetched.getTenant().getId());

    }



}
