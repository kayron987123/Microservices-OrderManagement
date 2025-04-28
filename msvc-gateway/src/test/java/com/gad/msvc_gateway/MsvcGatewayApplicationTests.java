package com.gad.msvc_gateway;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
@Import(MsvcGatewayApplicationTests.TestSecurityConfig.class)
class MsvcGatewayApplicationTests {

	@Test
	void contextLoads() {
	}

	@TestConfiguration
	public static class TestSecurityConfig {
		@Bean
		public SecurityWebFilterChain testSecurityFilterChain(ServerHttpSecurity http) {
			return http
					.csrf(ServerHttpSecurity.CsrfSpec::disable)
					.cors(ServerHttpSecurity.CorsSpec::disable)
					.authorizeExchange(exchanges -> exchanges
							.anyExchange().permitAll()  // Permite todas las rutas sin autenticación
					)
					.build();
		}
	}
}


