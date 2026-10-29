package com.bibliotheque.bibliotheque_api.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;

@Schema(description = "Mise à jour partielle d'un auteur : seuls les champs fournis sont modifiés")
public record AuteurUpdateRequest(

        @Schema(description = "Nouveau nom (2 à 30 caractères)", example = "Victor-Marie Hugo", nullable = true)
        @Size(min = 2, max = 30, message = "Le nom doit faire entre 2 et 30 caractères")
        String nom
){
}