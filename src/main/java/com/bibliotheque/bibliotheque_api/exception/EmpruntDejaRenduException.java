package com.bibliotheque.bibliotheque_api.exception;

public class EmpruntDejaRenduException extends RuntimeException {
    public EmpruntDejaRenduException(String message) {
        super(message);
    }
}
