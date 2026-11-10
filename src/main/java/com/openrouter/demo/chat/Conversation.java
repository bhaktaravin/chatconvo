	
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
@Table(name="conversations")
public class Conversation {
	
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	
	@ManyToOne(fetch = FetchType.LAZY, optional = false) 
	@JoinColumn(name = "character_id")
	private ChatCharacter character;
	
	
	@Column(nullable = false, length = 200)
	private String model;
	
	@Column(nullable = false)
	private int maxReplyTokens = 600;
	 
	
	@Column(nullable = false)
	private double temperature = 0.8;
	
	@Column(nullable = false, updatable =false)
	private Instant createdAt = Instant.now();
	
	
	
	protected Conversation() {}
	

	public Conversation(ChatCharacter character, String model) {
	
		this.character = character;
		this.model = model;
	}

	

	public Long getId() {
		return id;
	}



	public ChatCharacter getCharacter() {
		return character;
	}

	public void setCharacter(ChatCharacter character) {
		this.character = character;
	}

	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}

	public int getMaxReplyTokens() {
		return maxReplyTokens;
	}

	public void setMaxReplyTokens(int maxReplyTokens) {
		this.maxReplyTokens = maxReplyTokens;
	}

	public double getTemperature() {
		return temperature;
	}

	public void setTemperature(double temperature) {
		this.temperature = temperature;
	}

	public Instant getCreatedAt() {
		return createdAt;
	}


	
	
	

}
