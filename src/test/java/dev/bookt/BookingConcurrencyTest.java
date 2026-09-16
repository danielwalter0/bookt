package dev.bookt;

import dev.bookt.booking.BookingConflictException;
import dev.bookt.booking.BookingService;
import dev.bookt.booking.CreateBookingRequest;
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
import org.testcontainers.utility.DockerImageName;

import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Testcontainers
@SpringBootTest
public class BookingConcurrencyTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(DockerImageName.parse("postgres:16-alpine"));

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private BookingService bookingService;

    @Autowired
    private TenantRepository tenantRepository;

    @Autowired
    private ResourceRepository resourceRepository;

    @Test
    void onlyOneBookingSucceedsUnderConcurrentLoad() throws InterruptedException {
        // 1. SETUP
        Tenant tenant = new Tenant("Test Tenant", "Test Hash", null);
        tenant = tenantRepository.save(tenant);

        Resource resource = new Resource("Test Resource", tenant, 30, LocalTime.of(8,0), LocalTime.of(22,0));
        resource = resourceRepository.save(resource);
        UUID resourceId = resource.getId();

        OffsetDateTime startsAt = OffsetDateTime.now().plusDays(1).withHour(10).withMinute(0).withSecond(0).withNano(0);
        OffsetDateTime endsAt = startsAt.plusMinutes(30);

        int threadCount = 500;

        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch startGate = new CountDownLatch(1);
        CountDownLatch finishLatch = new CountDownLatch(threadCount);
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger conflictCount = new AtomicInteger(0);


        // 2. SUBMIT TASKS
        for (int i = 0; i < threadCount; i++) {
            executorService.execute(() -> {
                try {
                    startGate.await();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

                try{
                    CreateBookingRequest request = new CreateBookingRequest(resourceId, UUID.randomUUID(), startsAt, endsAt);
                    bookingService.createBooking(request);
                    successCount.incrementAndGet();
                } catch (BookingConflictException e){
                    conflictCount.incrementAndGet();
                } finally {
                    finishLatch.countDown();
                }

            });
        }

        // 3. PULL THE TRIGGER
        startGate.countDown();

        // 4. WAIT FOR EVERYONE TO FINISH
        boolean testCompleted = finishLatch.await(60, TimeUnit.SECONDS);
        assertTrue(testCompleted, "Not all threads finished within the timeout");

        // 5. SHUT DOWN THE POOL
        executorService.shutdown();

        // 6. ASSERTIONS
        assertEquals(1, successCount.get());
        assertEquals(threadCount - 1, conflictCount.get());
    }
}
