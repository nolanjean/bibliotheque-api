package com.bibliotheque.bibliotheque_api.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Schema(description = "Mise à jour partielle d'un membre : seuls les champs fournis sont modifiés. Le rôle ne peut pas être modifié par ce biais.")
public record MembreUpdateRequest(

        @Schema(description = "Nouveau nom", example = "Jean Martin", nullable = true)
        @Pattern(regexp = ".*\\S.*", message = "Le nom ne peut pas être vide")
        String nom,

        @Schema(description = "Nouvel email (doit être unique)", example = "jean.martin@email.com", nullable = true)
        @Email(message = "Email invalide")
        String email,

        @Schema(description = "Nouveau mot de passe (8 caractères minimum)", example = "nouveaumdp123", nullable = true)
        @Size(min = 8, message = "Le mot de passe doit contenir au moins 8 caractères")
        String motDePasse
) {
}