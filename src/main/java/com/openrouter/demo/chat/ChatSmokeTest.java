package com.openrouter.demo.chat;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * Runs one short conversation with Jamie at startup, through the real ChatService.
 * Only active when app.chat.smoke-test=true. Each run creates a new Jamie row and
 * makes 2 model calls, so switch it off when you're done.
 */
@Component
@ConditionalOnProperty(name = "app.chat.smoke-test", havingValue = "true")
class ChatSmokeTest implements CommandLineRunner {

    private final ChatCharacterRepository characters;
    private final ConversationRepository conversations;
    private final ChatService chatService;

    ChatSmokeTest(ChatCharacterRepository characters,
                  ConversationRepository conversations,
                  ChatService chatService) {
        this.characters = characters;
        this.conversations = conversations;
        this.chatService = chatService;
    }

    @Override
    public void run(String... args) {
        ChatCharacter jamie = new ChatCharacter("Jamie Rivera", """
                Jamie, 26, has been your best friend since you were both 14, when you got paired \
                for a disastrous science project and laughed your way through a failing grade. \
                Jamie works as a sound engineer, is fiercely loyal, quick with sarcasm, and hides \
                nervousness behind jokes. You two have a standing Friday movie night, a running bit \
                about rating diner pancakes, and a shared playlist neither of you will admit is \
                mostly love songs.

                Secretly, Jamie has had feelings for you for about a year and is terrified of \
                ruining the friendship. When nervous, Jamie over-explains, fidgets with their \
                hoodie strings, and changes the subject with a joke.""");

        jamie.setScenario("""
                It's Friday movie night at Jamie's apartment. Jamie just found out you've been \
                asked on a date by someone else, and is trying very hard to act normal about it.

                This is a slow-burn story. Build tension gradually through small moments, \
                hesitation, and things left unsaid. Jamie does not confess feelings unless the \
                user's actions have clearly earned it over many exchanges.""");

        jamie.setGreeting("""
                *Jamie tosses you the blanket without looking up from the popcorn bowl.* \
                "So. I picked the movie, and before you complain, yes, it's the one with the dog, \
                and no, I won't cry this time." *A pause, a little too long.* "Also, uh. Heard \
                you've got a date Saturday. That's... great. That's really great.\"""");

        jamie.setExampleDialogue("""
                You: Are you okay? You've been weird all night.
                Jamie: "Weird? Me? I'm the most normal person in this room. You're the one who \
                put pineapple on pizza." *Jamie laughs, but it doesn't quite reach their eyes.*""");

        jamie = characters.save(jamie);

        Conversation conversation = chatService.startConversation(jamie.getId());
        conversation.setMaxReplyTokens(800);   // room for a full reply, so we don't hit "length"
        conversations.save(conversation);

        System.out.println("=== Jamie (greeting) ===");
        System.out.println(jamie.getGreeting());

        send(conversation, "Yeah, Saturday. Honestly, I'm not even sure I want to go.");

        // Memory test: the answer ("the one with the dog") is only in the greeting.
        send(conversation, "Wait, which movie did you say we're watching?");
    }

    private void send(Conversation conversation, String text) {
        System.out.println();
        System.out.println("=== You ===");
        System.out.println(text);

        try {
            Message reply = chatService.sendMessage(conversation.getId(), text);
            System.out.println("=== Jamie ===");
            System.out.println(reply.getContent());
            System.out.println("[finish: " + reply.getFinishReason()
                    + " | tokens in/out: " + reply.getPromptTokens() + " / " + reply.getCompletionTokens() + "]");
        } catch (ModelUnavailableException e) {
            // A busy model shouldn't crash the app: print it and carry on.
            System.out.println("!!! " + e.getMessage() + " (" + e.getCause() + ")");
        }
    }
}
