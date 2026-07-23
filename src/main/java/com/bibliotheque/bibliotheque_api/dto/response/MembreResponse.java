package com.bibliotheque.bibliotheque_api.dto.response;

import com.bibliotheque.bibliotheque_api.enums.Role;

public record MembreResponse(
        Long id,
        String nom,
        String email,
        Role role
) {
}
