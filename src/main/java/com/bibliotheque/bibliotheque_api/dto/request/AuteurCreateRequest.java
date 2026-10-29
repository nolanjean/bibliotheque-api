package com.bibliotheque.bibliotheque_api.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AuteurCreateRequest(

        @Schema(description = "Nom de l'auteur (3 à 30 caractères)", example = "Victor Hugo")
        @NotBlank(message = "Le nom est obligatoire")
        @Size(min = 3, message = "Le nom doit faire entre 3 et 30 caractères", max = 30)
        String nom
) {
}