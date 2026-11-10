package com.openrouter.demo.openrouter;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public final class OpenRouterApi {
	
	private OpenRouterApi() {
	
	}
	
	
	
	  public record ChatMessage(String role, String content) {
	    }

	    public record ChatRequest(
	            String model,
	            List<ChatMessage> messages,
	            @JsonProperty("max_tokens") Integer maxTokens,
	            Double temperature) {
	    }

	    public record ChatResponse(List<Choice> choices, Usage usage) {
	    }

	    public record Choice(ChatMessage message, @JsonProperty("finish_reason") String finishReason) {
	    }

	    public record Usage(
	            @JsonProperty("prompt_tokens") int promptTokens,
	            @JsonProperty("completion_tokens") int completionTokens) {
	    }

}
