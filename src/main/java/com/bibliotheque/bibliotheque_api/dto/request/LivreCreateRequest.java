package com.bibliotheque.bibliotheque_api.dto.request;

public record LivreCreateRequest(
        String titre,
        String isbn,
        int nombreExemplaires,
        Long auteurId
) {
}
