package com.bibliotheque.bibliotheque_api.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "Auteur renvoyé par l'API")
public record AuteurResponse(

        @Schema(description = "Identifiant de l'auteur", example = "1")
        Long id,

        @Schema(description = "Nom de l'auteur", example = "Victor Hugo")
        String nom,

        @Schema(description = "Titres des livres de cet auteur", example = "[\"Les Misérables\", \"Notre-Dame de Paris\"]")
        List<String> titresLivres
) {
}