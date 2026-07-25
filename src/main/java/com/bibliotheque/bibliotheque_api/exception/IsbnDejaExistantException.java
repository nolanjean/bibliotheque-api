package com.bibliotheque.bibliotheque_api.exception;

public class IsbnDejaExistantException extends RuntimeException {
    public IsbnDejaExistantException(String message) {
        super(message);
    }
}
