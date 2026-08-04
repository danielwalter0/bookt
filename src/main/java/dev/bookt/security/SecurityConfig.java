package dev.bookt.security;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) // turn off CSRF protection (not needed for a stateless API)
            .authorizeHttpRequests(authorizeRequests -> authorizeRequests.anyRequest().permitAll()); // allow all requests through, no auth required (placeholder)
        return http.build(); // build and return the security config
    }
}
