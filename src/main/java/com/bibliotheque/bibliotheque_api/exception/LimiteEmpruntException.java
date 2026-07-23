package com.bibliotheque.bibliotheque_api.exception;

public class LimiteEmpruntException extends RuntimeException {
    public LimiteEmpruntException(String message) {
        super(message);
    }
}
