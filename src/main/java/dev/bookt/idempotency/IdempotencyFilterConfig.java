package dev.bookt.idempotency;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class IdempotencyFilterConfig {

    @Bean
    public FilterRegistrationBean<IdempotencyFilter> idempotencyFilter(IdempotencyKeyRepository idempotencyKeyRepository) {
        FilterRegistrationBean<IdempotencyFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new IdempotencyFilter(idempotencyKeyRepository));
        registrationBean.addUrlPatterns("/bookings", "/bookings/hold", "/bookings/*/confirm");
        return registrationBean;
    }
}