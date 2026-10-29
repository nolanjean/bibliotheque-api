package com.bibliotheque.bibliotheque_api.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Map;

@Schema(description = "Format unique de toutes les erreurs de l'API")
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiError(

        @Schema(description = "Code HTTP de l'erreur", example = "400")
        int status,

        @Schema(description = "Message d'erreur lisible", example = "Données invalides")
        String erreur,

        @Schema(description = "Détail par champ, présent uniquement pour les erreurs de validation (400)",
                example = "{\"email\": \"Email invalide\"}",
                nullable = true)
        Map<String, String> champs) {

    public ApiError(int status, String erreur) {
        this(status, erreur, null);
    }
}