package com.openrouter.demo.openrouter;

import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import com.openrouter.demo.openrouter.OpenRouterApi.ChatMessage;
import com.openrouter.demo.openrouter.OpenRouterApi.ChatRequest;
import com.openrouter.demo.openrouter.OpenRouterApi.ChatResponse;

@Component
@ConditionalOnProperty(name = "app.openrouter.smoke-test", havingValue = "true")
class OpenRouterSmokeTest implements CommandLineRunner {

    private final OpenRouterClient client;
    private final OpenRouterProperties props;

    OpenRouterSmokeTest(OpenRouterClient client, OpenRouterProperties props) {
        this.client = client;
        this.props = props;
    }

    @Override
    public void run(String... args) {
        ChatRequest request = new ChatRequest(
                props.defaultModel(),
                List.of(
                        new ChatMessage("system", "You are Captain Mara, a sarcastic starship pilot. Stay in character."),
                        new ChatMessage("user", "We're out of fuel. What now?")),
                800,
                0.8);

        ChatResponse response = client.chat(request);

        System.out.println("=== Reply ===");
        System.out.println(response.choices().get(0).message().content());
        System.out.println("finish_reason: " + response.choices().get(0).finishReason());
        System.out.println("tokens in/out: " + response.usage().promptTokens() + " / " + response.usage().completionTokens());
    }
}