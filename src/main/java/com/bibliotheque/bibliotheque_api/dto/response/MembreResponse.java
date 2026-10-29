package com.bibliotheque.bibliotheque_api.dto.response;

import com.bibliotheque.bibliotheque_api.enums.Role;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Membre renvoyé par l'API (le mot de passe n'est jamais renvoyé)")
public record MembreResponse(

        @Schema(description = "Identifiant du membre", example = "1")
        Long id,

        @Schema(description = "Nom du membre", example = "Jean Dupont")
        String nom,

        @Schema(description = "Adresse email (identifiant de connexion)", example = "jean@email.com")
        String email,

        @Schema(description = "Rôle du membre (ADMIN ou MEMBRE)")
        Role role
) {
}