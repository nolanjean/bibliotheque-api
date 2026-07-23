package com.bibliotheque.bibliotheque_api.exception;

public class LivreIndisponibleException extends RuntimeException {
    public LivreIndisponibleException(String message) {
        super(message);
    }
}
