package com.openrouter.demo.chat;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClientException;

import com.openrouter.demo.openrouter.OpenRouterApi.ChatMessage;
import com.openrouter.demo.openrouter.OpenRouterApi.ChatRequest;
import com.openrouter.demo.openrouter.OpenRouterApi.ChatResponse;
import com.openrouter.demo.openrouter.OpenRouterApi.Choice;
import com.openrouter.demo.openrouter.OpenRouterClient;
import com.openrouter.demo.openrouter.OpenRouterProperties;

/**
 * The chat orchestrator from the architecture map. Every user message goes through
 * sendMessage(): save it, build the prompt, call the model, save the reply.
 */
@Service
public class ChatService {

    private final ChatCharacterRepository characters;
    private final ConversationRepository conversations;
    private final MessageRepository messages;
    private final PromptBuilder promptBuilder;
    private final OpenRouterClient client;
    private final OpenRouterProperties props;

    // One constructor = Spring injects everything automatically, no @Autowired needed.
    public ChatService(ChatCharacterRepository characters,
                       ConversationRepository conversations,
                       MessageRepository messages,
                       PromptBuilder promptBuilder,
                       OpenRouterClient client,
                       OpenRouterProperties props) {
        this.characters = characters;
        this.conversations = conversations;
        this.messages = messages;
        this.promptBuilder = promptBuilder;
        this.client = client;
        this.props = props;
    }

    /**
     * Starts a new chat with a character. If the character has a greeting, it becomes the
     * first message, so the model sees how the character opened the scene.
     *
     * @Transactional: both inserts succeed together or neither does.
     */
    @Transactional
    public Conversation startConversation(Long characterId) {
        ChatCharacter character = characters.findById(characterId)
                .orElseThrow(() -> new IllegalArgumentException("No character with id " + characterId));

        Conversation conversation = conversations.save(new Conversation(character, props.defaultModel()));

        String greeting = character.getGreeting();
        if (greeting != null && !greeting.isBlank()) {
            messages.save(new Message(conversation, "assistant", greeting));
        }
        return conversation;
    }

    /**
     * Sends the user's message and returns the saved reply.
     *
     * Deliberately NOT @Transactional: the model call can take 10+ seconds, and holding a
     * database transaction (and connection) open that long doesn't scale. Each save() below
     * runs in its own short transaction instead.
     */
    public Message sendMessage(Long conversationId, String userText) {
        // Loads the conversation AND its character in one query (see the @EntityGraph method).
        // Without that, conversation.getCharacter() would fail here with a
        // LazyInitializationException, because there's no open transaction.
        Conversation conversation = conversations.findWithCharacterById(conversationId)
                .orElseThrow(() -> new IllegalArgumentException("No conversation with id " + conversationId));

        // 1. Save the user's message first. If the model fails, it isn't lost.
        messages.save(new Message(conversation, "user", userText));

        // 2. Build the prompt from the character and the full history (including that message).
        List<Message> history = messages.findByConversationIdOrderByIdAsc(conversationId);
        List<ChatMessage> prompt = promptBuilder.build(conversation.getCharacter(), history);

        // 3. Call the model. RestClientException covers HTTP errors (429, 401, 500...)
        //    and network problems (timeouts, connection refused).
        ChatResponse response;
        try {
            response = client.chat(new ChatRequest(
                    conversation.getModel(),
                    prompt,
                    conversation.getMaxReplyTokens(),
                    conversation.getTemperature()));
        } catch (RestClientException e) {
            throw new ModelUnavailableException("The model didn't respond. Please try again.", e);
        }

        if (response == null || response.choices() == null || response.choices().isEmpty()) {
            throw new ModelUnavailableException("The model returned an empty response. Please try again.");
        }

        // 4. Save the reply with its token counts and finish reason.
        Choice choice = response.choices().get(0);

        // content can be null, e.g. when the model spends the whole token budget thinking.
        // The column is NOT NULL, so store an empty string rather than crash.
        String content = choice.message() != null && choice.message().content() != null
                ? choice.message().content()
                : "";

        Message reply = new Message(conversation, "assistant", content);
        reply.setFinishReason(choice.finishReason());
        if (response.usage() != null) {
            reply.setPromptTokens(response.usage().promptTokens());
            reply.setCompletionTokens(response.usage().completionTokens());
        }
        return messages.save(reply);
    }
}
