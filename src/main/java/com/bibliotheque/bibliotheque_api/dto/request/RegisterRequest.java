package com.bibliotheque.bibliotheque_api.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest(

        @Schema(description = "Nom du membre", example = "Jean Dupont")
        @NotBlank(message = "Le nom est obligatoire")
        String nom,

        @Schema(description = "Email, unique, servira d'identifiant de connexion", example = "jean@email.com")
        @NotBlank(message = "L'email est obligatoire")
        @Email(message = "Email invalide")
        String email,

        @Schema(description = "Mot de passe (8 caractères minimum)", example = "motdepasse123")
        @NotBlank(message = "Le mot de passe est obligatoire")
        @Size(min = 8, message = "Le mot de passe doit contenir au moins 8 caractères")
        String motDePasse
) {
}