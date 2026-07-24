package com.bibliotheque.bibliotheque_api.dto.response;

import com.bibliotheque.bibliotheque_api.enums.StatutEmprunt;

import java.time.LocalDate;

public record EmpruntResponse(
        Long id,
        String membreNom,
        String livreTitre,
        LocalDate dateEmprunt,
        LocalDate dateRetourPrevue,
        StatutEmprunt statut
) {
}
