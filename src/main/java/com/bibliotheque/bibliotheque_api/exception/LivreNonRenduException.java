package com.bibliotheque.bibliotheque_api.exception;

public class LivreNonRenduException extends RuntimeException {
    public LivreNonRenduException(String message) {
        super(message);
    }
}
