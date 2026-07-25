package com.bibliotheque.bibliotheque_api.dto.request;

public record RegisterRequest(
        String nom,
        String email,
        String motDePasse
) {
}
