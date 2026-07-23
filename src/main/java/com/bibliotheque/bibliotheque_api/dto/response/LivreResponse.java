package com.bibliotheque.bibliotheque_api.dto.response;

public record LivreResponse(
        Long id,
        String titre,
        String isbn,
        int nombreExemplaires,
        String auteurNom) {
}
