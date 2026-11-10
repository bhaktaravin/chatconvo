package com.openrouter.demo.chat;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "messages")
public class Message {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id; 
	
	
	@ManyToOne(fetch = FetchType.LAZY, optional = false) 
	@JoinColumn(name = "conversation_id")
	private Conversation conversation; 
	
	
	
	@Column(nullable = false, length = 16)
	private String role;
	
	@Column(nullable = false)
	private String content;
	
	
	private Integer promptTokens;
	
	private Integer completionTokens;
	
	@Column(length=32)
	private String finishReason;
	
	@Column(nullable = false, updatable =false)
	private Instant createdAt = Instant.now();

	
	protected Message () {}
	
	
	public Message(Conversation conversation, String role, String content) {
		this.conversation = conversation; 
		this.role = role;
		this.content = content;
	}



	public Long getId() {
		return id;
	}


	public Conversation getConversation() {
		return conversation;
	}

	public void setConversation(Conversation conversation) {
		this.conversation = conversation;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public Integer getPromptTokens() {
		return promptTokens;
	}

	public void setPromptTokens(Integer promptTokens) {
		this.promptTokens = promptTokens;
	}

	public Integer getCompletionTokens() {
		return completionTokens;
	}

	public void setCompletionTokens(Integer completionTokens) {
		this.completionTokens = completionTokens;
	}

	public String getFinishReason() {
		return finishReason;
	}

	public void setFinishReason(String finishReason) {
		this.finishReason = finishReason;
	}

	public Instant getCreatedAt() {
		return createdAt;
	}

	
	 









}
