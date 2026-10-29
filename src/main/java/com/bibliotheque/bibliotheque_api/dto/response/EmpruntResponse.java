package com.bibliotheque.bibliotheque_api.dto.response;

import com.bibliotheque.bibliotheque_api.enums.StatutEmprunt;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;

@Schema(description = "Emprunt renvoyé par l'API")
public record EmpruntResponse(

        @Schema(description = "Identifiant de l'emprunt", example = "1")
        Long id,

        @Schema(description = "Nom du membre qui a emprunté", example = "Jean Dupont")
        String membreNom,

        @Schema(description = "Titre du livre emprunté", example = "Les Misérables")
        String livreTitre,

        @Schema(description = "Date de l'emprunt", example = "2026-10-29")
        LocalDate dateEmprunt,

        @Schema(description = "Date de retour prévue", example = "2026-11-12")
        LocalDate dateRetourPrevue,

        @Schema(description = "Statut de l'emprunt (EN_COURS ou RENDU)")
        StatutEmprunt statut
) {
}