package com.openrouter.demo.chat;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.openrouter.demo.openrouter.OpenRouterApi.ChatMessage;


@Component
public class PromptBuilder {
	
	public List<ChatMessage> build(ChatCharacter character, List<Message> history){
			StringBuilder system = new StringBuilder(); 
			system.append("You are ").append(character.getName()) 
			.append(". Stay in character and never mention being an AI"); 
			
			system.append("\n\nPersona:\n").append(character.getPersona());
			if (hasText(character.getScenario())) {
	            system.append("\n\nScenario:\n").append(character.getScenario());
	        }
	        if (hasText(character.getExampleDialogue())) {
	            system.append("\n\nExample dialogue:\n").append(character.getExampleDialogue());
	        }
	        
	     // --- Job 2: the list the model receives ---
	        List<ChatMessage> prompt = new ArrayList<>();
	        prompt.add(new ChatMessage("system", system.toString()));   // the string goes in first

	        for (Message m : history) {                                  // then every past message, in order
	            prompt.add(new ChatMessage(m.getRole(), m.getContent()));
	        }
	        return prompt;
	    }
			
	
	  private boolean hasText(String s) {
	        return s != null && !s.isBlank();
	    }

}
