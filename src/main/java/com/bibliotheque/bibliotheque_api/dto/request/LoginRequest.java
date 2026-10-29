package com.bibliotheque.bibliotheque_api.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginRequest(

        @Schema(description = "Email du compte", example = "jean@email.com")
        @NotBlank(message = "L'email est obligatoire")
        @Email(message = "Email invalide")
        String email,

        @Schema(description = "Mot de passe du compte", example = "motdepasse123")
        @NotBlank(message = "Le mot de passe est obligatoire")
        String motDePasse
) {
}