package com.techcross.security.jwt.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class BeanConfig {
	@Value("${server.port}")
	private int serverPort;

	@Bean
	public WebClient getWebClient() {
		return WebClient.builder().baseUrl("http://localhost:" + serverPort)
				.defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE).build();
	}
	
}
