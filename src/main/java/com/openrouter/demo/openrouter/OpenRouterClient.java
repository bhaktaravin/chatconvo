package com.openrouter.demo.openrouter;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import com.openrouter.demo.openrouter.OpenRouterApi.ChatRequest;
import com.openrouter.demo.openrouter.OpenRouterApi.ChatResponse;


@Component
public class OpenRouterClient {
	
	private final RestClient http; 
	
	
	public OpenRouterClient(RestClient.Builder builder, OpenRouterProperties props) {
	    String apiKey = props.apiKey() == null ? "" : props.apiKey().strip();
	    if (apiKey.isEmpty()) {
	        throw new IllegalStateException("OPENROUTER_API_KEY is not set");
	    }
	    this.http = builder
	            .baseUrl(props.baseUrl())
	            .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
	            .build();
	}
	    public ChatResponse chat(ChatRequest request) {
	        return http.post()
	                .uri("/chat/completions")
	                .contentType(MediaType.APPLICATION_JSON)
	                .body(request)
	                .retrieve()
	                .body(ChatResponse.class);
	    }
	
	
	

}
