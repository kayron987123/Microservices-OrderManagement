package com.gad.msvc_gateway.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.web.server.SecurityWebFilterChain;
import reactor.core.publisher.Mono;

import java.util.Collection;
import java.util.stream.Collectors;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
public class SecurityConfig {
    @Bean
    @Profile("!test")
    SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http
                .authorizeExchange(auth -> auth
                        .pathMatchers(HttpMethod.GET, "/api/v1/products/**").permitAll()
                        .pathMatchers(HttpMethod.GET,
                                "/api/v1/customers/{uuid}",
                                "/api/v1/customers/email/{email}",
                                "/api/v1/order-details/{uuid}",
                                "/api/v1/orders/{uuid}").hasRole("ADMIN")
                        .pathMatchers(HttpMethod.POST, "/api/v1/customers").permitAll()
                        .pathMatchers(HttpMethod.POST, "/api/v1/orders", "/api/v1/order-details").hasRole("USER")
                        .pathMatchers(HttpMethod.PUT, "/api/v1/customers", "/api/v1/orders").hasRole("USER")
                        .pathMatchers(HttpMethod.DELETE, "/api/v1/orders/{uuid}").hasRole("USER")
                        .pathMatchers(HttpMethod.POST, "/actuator/refresh").permitAll()
                        .anyExchange().denyAll()
                )
                .cors(ServerHttpSecurity.CorsSpec::disable)
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .oauth2Login(withDefaults())
                .oauth2Client(withDefaults())
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(
                        jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter())
                ))
                .build();
    }

    private Converter<Jwt, Mono<AbstractAuthenticationToken>> jwtAuthenticationConverter() {
        return source -> {
            Collection<String> roles = source.getClaimAsStringList("roles");
            Collection<GrantedAuthority> authorities = roles.stream()
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList());

            return Mono.just(new JwtAuthenticationToken(source, authorities));
        };
    }
}
