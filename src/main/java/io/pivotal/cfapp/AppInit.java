package io.pivotal.cfapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.autoconfigure.security.reactive.EndpointRequest;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.reactive.PathRequest;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
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
				.authorizeExchange()
					.matchers(
						PathRequest.toStaticResources().atCommonLocations(),
						EndpointRequest.toAnyEndpoint())
						.permitAll()
					.pathMatchers("/accounting/**","/snapshot/**", "/space-users", "/users/**")
						.permitAll()
					.and().build();
	}
}