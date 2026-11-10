package com.openrouter.demo.openrouter;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.openrouter")

public record OpenRouterProperties(String baseUrl, String apiKey, String defaultModel) {
		
}

