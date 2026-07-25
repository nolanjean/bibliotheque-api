package com.bibliotheque.bibliotheque_api.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RessourceNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleNotFound(RessourceNotFoundException ex) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(Map.of("erreur", ex.getMessage()));
    }

    @ExceptionHandler({
            LimiteEmpruntException.class,
            LivreIndisponibleException.class,
            EmpruntDejaRenduException.class,
            LivreNonRenduException.class,
            AuteurPossedeLivresException.class,
            MembrePossedeEmpruntsException.class,
            EmailDejaUtiliseException.class,
            IsbnDejaExistantException.class
    })
    public ResponseEntity<Map<String, String>> handleConflict(RuntimeException ex) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(Map.of("erreur", ex.getMessage()));
    }

    @ExceptionHandler(IdentifiantsInvalidesException.class)
    public ResponseEntity<Map<String, String>> handleUnauthorized(IdentifiantsInvalidesException ex) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("erreur", ex.getMessage()));
    }

}
