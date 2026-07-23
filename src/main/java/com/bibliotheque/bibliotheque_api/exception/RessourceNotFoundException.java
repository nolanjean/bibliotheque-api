package com.bibliotheque.bibliotheque_api.exception;

public class RessourceNotFoundException extends RuntimeException {
    public RessourceNotFoundException(String entite, Long id) {
        super(entite + " introuvable avec l'id " + id);
    }
}
