package com.openrouter.demo.chat;

import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConversationRepository extends JpaRepository<Conversation, Long>{
	
	@EntityGraph(attributePaths = "character")
	Optional<Conversation> findWithCharacterById(Long id);
	
}
