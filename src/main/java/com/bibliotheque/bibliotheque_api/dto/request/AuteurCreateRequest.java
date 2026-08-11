package com.bibliotheque.bibliotheque_api.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AuteurCreateRequest(

        @NotBlank(message = "Le nom est obligatoire")
        @Size(min = 3, message = "Le nom doit faire entre 2 et 30 caractères", max = 30)
        String nom
) {
}
