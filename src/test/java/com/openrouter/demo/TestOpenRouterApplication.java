package com.openrouter.demo;

import org.springframework.boot.SpringApplication;

public class TestOpenRouterApplication {

	public static void main(String[] args) {
		SpringApplication.from(OpenRouterApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
