package com.bibliotheque.bibliotheque_api.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

@Schema(description = "Mise à jour partielle d'un livre : tous les champs sont optionnels, seuls ceux fournis sont modifiés")
public record LivreUpdateRequest(

        @Schema(description = "Nouveau titre (2 à 40 caractères)", example = "Notre-Dame de Paris", nullable = true)
        @Size(min = 2, max = 40, message = "Le titre doit faire entre 2 et 40 caractères")
        String titre,

        @Schema(description = "Nouvel ISBN, 10 ou 13 chiffres sans tiret", example = "9782070409189", nullable = true)
        @Pattern(regexp = "\\d{10}|\\d{13}", message = "L'ISBN doit contenir 10 ou 13 chiffres")
        String isbn,

        @Schema(description = "Nouveau nombre d'exemplaires (0 ou plus)", example = "5", nullable = true)
        @PositiveOrZero(message = "Le nombre d'exemplaires ne peut pas être négatif")
        Integer nombreExemplaires,

        @Schema(description = "Identifiant du nouvel auteur (doit exister)", example = "1", nullable = true)
        Long auteurId
) {
}