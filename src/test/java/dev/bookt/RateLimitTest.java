package dev.bookt;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.resttestclient.TestRestTemplate;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureTestRestTemplate;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.*;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestRestTemplate
public class RateLimitTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(DockerImageName.parse("postgres:16-alpine"));

    @Container
    static GenericContainer<?> redis = new GenericContainer<>(DockerImageName.parse("redis:7-alpine")).withExposedPorts(6379).withCommand("redis-server", "--requirepass", "secret");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.data.redis.host", redis::getHost);
        registry.add("spring.data.redis.port", () -> redis.getMappedPort(6379));
        registry.add("spring.data.redis.password", () -> "secret");
    }

    @Autowired
    private TestRestTemplate restTemplate;

    @Value("${rate-limit.max-requests}")
    private int maxRequests;

    @Test
    void onlyAllowedNumberOfRequestsSucceeds() throws InterruptedException {

        //this loop is designed to check ONE request which goes out of the allowed request attempts
        for(int i = 0; i < maxRequests+1; i++){
            HttpHeaders headers = new HttpHeaders();
            headers.set("Idempotency-Key", UUID.randomUUID().toString());
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<String> request = new HttpEntity<>("{}", headers);

            String url = "/bookings/00000000-0000-0000-0000-000000000000/confirm";
            ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);
            HttpStatusCode status = response.getStatusCode();

            if(i < maxRequests) {
                assertEquals(HttpStatus.NOT_FOUND, status);
            }
            else if(i == maxRequests) {
                assertEquals(HttpStatus.TOO_MANY_REQUESTS, status);
            }

        }
    }

}
