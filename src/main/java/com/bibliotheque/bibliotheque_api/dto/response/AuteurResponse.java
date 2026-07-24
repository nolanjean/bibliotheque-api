package com.bibliotheque.bibliotheque_api.dto.response;

import java.util.List;

public record AuteurResponse(
        Long id,
        String nom,
        List<String> titresLivres
) {
}
