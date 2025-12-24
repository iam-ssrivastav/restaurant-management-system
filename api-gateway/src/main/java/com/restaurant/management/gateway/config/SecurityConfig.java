package com.restaurant.management.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(exchanges -> exchanges
                        .pathMatchers("/actuator/**", "/auth/**", "/v3/api-docs/**", "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/api/v1/orders/v3/api-docs/**",
                                "/api/v1/restaurants/v3/api-docs/**",
                                "/api/v1/kitchen/v3/api-docs/**",
                                "/api/v1/delivery/v3/api-docs/**",
                                "/api/v1/accounting/v3/api-docs/**")
                        .permitAll()
                        .anyExchange().permitAll());

        return http.build();
    }
}
