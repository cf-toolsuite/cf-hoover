package org.cftoolsuite.cfapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.boot.security.autoconfigure.actuate.web.reactive.EndpointRequest;
import org.springframework.boot.security.autoconfigure.web.reactive.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@SpringBootApplication
@ConfigurationPropertiesScan
public class AppInit {

	public static void main(String[] args) {
		SpringApplication.run(AppInit.class, args);
	}

	@Bean
	public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
		return http
				.authorizeExchange(exchanges -> exchanges
					.matchers(
						PathRequest.toStaticResources().atCommonLocations(),
						EndpointRequest.toAnyEndpoint())
						.permitAll()
					.pathMatchers("/accounting/**","/snapshot/**", "/space-users", "/users/**", "/collect")
						.permitAll()
				).build();
	}
}