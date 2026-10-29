package com.bibliotheque.bibliotheque_api.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

public record LivreCreateRequest(

        @Schema(description = "Titre du livre (2 à 40 caractères)", example = "Les Misérables")
        @NotBlank(message = "Le titre est obligatoire")
        @Size(min = 2, message = "Le titre doit faire entre 2 et 40 caractères", max = 40)
        String titre,

        @Schema(description = "ISBN unique, 10 ou 13 chiffres sans tiret", example = "9782070409189")
        @NotBlank(message = "L'ISBN est obligatoire")
        @Pattern(regexp = "\\d{10}|\\d{13}", message = "L'ISBN doit contenir 10 ou 13 chiffres")
        String isbn,

        @Schema(description = "Nombre d'exemplaires du livre (0 ou plus)", example = "3")
        @PositiveOrZero(message = "Le nombre d'exemplaires ne peut pas être négatif")
        int nombreExemplaires,

        @Schema(description = "Identifiant d'un auteur existant", example = "1")
        @NotNull(message = "L'auteur est obligatoire")
        Long auteurId
) {
}