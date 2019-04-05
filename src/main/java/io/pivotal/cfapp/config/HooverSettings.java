package io.pivotal.cfapp.config;

import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

@Data
@RefreshScope
@Configuration
@ConfigurationProperties(prefix = "cf")
public class HooverSettings {

	private Map<String, String> butlers;
	private boolean sslValidationSkipped;

}
