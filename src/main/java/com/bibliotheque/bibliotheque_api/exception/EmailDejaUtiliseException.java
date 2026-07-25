package com.bibliotheque.bibliotheque_api.exception;

public class EmailDejaUtiliseException extends RuntimeException {
    public EmailDejaUtiliseException(String message) {
        super(message);
    }
}
