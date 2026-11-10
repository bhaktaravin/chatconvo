package com.openrouter.demo.chat;

/**
 * The model didn't give us a usable reply: rate limit (429), timeout, provider error,
 * or an empty response. The user's message is already saved, so they can simply retry.
 */
public class ModelUnavailableException extends RuntimeException {

    public ModelUnavailableException(String message) {
        super(message);
    }

    public ModelUnavailableException(String message, Throwable cause) {
        super(message, cause);
    }
}
