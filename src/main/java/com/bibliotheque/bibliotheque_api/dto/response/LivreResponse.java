package com.bibliotheque.bibliotheque_api.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Livre renvoyé par l'API")
public record LivreResponse(

        @Schema(description = "Identifiant du livre", example = "1")
        Long id,

        @Schema(description = "Titre du livre", example = "Les Misérables")
        String titre,

        @Schema(description = "ISBN (10 ou 13 chiffres)", example = "9782070409189")
        String isbn,

        @Schema(description = "Nombre d'exemplaires du livre", example = "3")
        int nombreExemplaires,

        @Schema(description = "Nom de l'auteur", example = "Victor Hugo")
        String auteurNom) {
}