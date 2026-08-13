package com.bibliotheque.bibliotheque_api.dto.request;

import jakarta.validation.constraints.*;

public record LivreUpdateRequest(
        @Size(min = 2, max = 40, message = "Le titre doit faire entre 2 et 40 caractères")
        String titre,

        @Pattern(regexp = "\\d{10}|\\d{13}", message = "L'ISBN doit contenir 10 ou 13 chiffres")
        String isbn,

        @PositiveOrZero
        Integer nombreExemplaires,

        Long auteurId
) {


}
