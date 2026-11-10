package com.openrouter.demo.chat;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Database access for ChatCharacter. Spring Data writes the implementation at startup.
 *
 * JpaRepository<ChatCharacter, Long> = entity type, id type. You get save, findById,
 * findAll, deleteById, count, ... for free.
 */
public interface ChatCharacterRepository extends JpaRepository<ChatCharacter, Long> {
}
