package com.bibliotheque.bibliotheque_api.dto.request;

import jakarta.validation.constraints.*;

public record LivreCreateRequest(

        @NotBlank(message = "Le titre est obligatoire")
        @Size(min = 2, message = "Le titre doit faire entre 2 et 40 caractères", max = 40)
        String titre,

        @NotBlank(message = "L'ISBN est obligatoire")
        @Pattern(regexp = "\\d{10}|\\d{13}", message = "L'ISBN doit contenir 10 ou 13 chiffres")
        String isbn,

        @PositiveOrZero
        int nombreExemplaires,

        @NotNull
        Long auteurId
) {
}
