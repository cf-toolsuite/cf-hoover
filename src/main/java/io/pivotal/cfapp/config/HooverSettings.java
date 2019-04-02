package io.pivotal.cfapp.config;

import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

@Data
@Configuration
@ConfigurationProperties(prefix = "cf")
public class HooverSettings {

	private Map<String, String> butlers;
	private boolean sslValidationSkipped;

}
