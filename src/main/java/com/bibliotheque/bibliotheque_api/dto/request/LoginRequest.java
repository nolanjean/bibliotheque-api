package com.bibliotheque.bibliotheque_api.dto.request;

public record LoginRequest(
        String email,
        String motDePasse
) {
}
