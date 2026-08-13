package com.bibliotheque.bibliotheque_api.dto.request;

import jakarta.validation.constraints.Size;

public record AuteurUpdateRequest(
        @Size(min = 2, max = 30, message = "Le nom doit faire entre 2 et 30 caractères")
        String nom
){
}
