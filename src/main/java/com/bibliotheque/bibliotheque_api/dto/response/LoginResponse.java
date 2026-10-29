package com.bibliotheque.bibliotheque_api.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Réponse d'une connexion réussie")
public record LoginResponse(

        @Schema(description = "Token JWT à envoyer dans l'en-tête `Authorization: Bearer {token}`",
                example = "eyJhbGciOiJIUzI1NiJ9.exemple.signature")
        String token,

        @Schema(description = "Rôle du membre connecté", example = "MEMBRE", allowableValues = {"ADMIN", "MEMBRE"})
        String role
) {
}