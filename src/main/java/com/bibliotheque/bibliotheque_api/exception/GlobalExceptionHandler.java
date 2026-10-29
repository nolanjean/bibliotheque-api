package com.bibliotheque.bibliotheque_api.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    public static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    private ResponseEntity<ApiError> reponse(HttpStatus status, String message) {
        return ResponseEntity.status(status).body(new ApiError(status.value(), message));
    }

    @ExceptionHandler(RessourceNotFoundException.class)
    public ResponseEntity<ApiError> handleNotFound(RessourceNotFoundException ex) {
        return reponse(HttpStatus.NOT_FOUND, ex.getMessage());
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
    public ResponseEntity<ApiError> handleConflict(RuntimeException ex) {
        return reponse(HttpStatus.CONFLICT, ex.getMessage());
    }

    @ExceptionHandler(IdentifiantsInvalidesException.class)
    public ResponseEntity<ApiError> handleUnauthorized(IdentifiantsInvalidesException ex) {
        return reponse(HttpStatus.UNAUTHORIZED, ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> champs = new LinkedHashMap<>();
        ex.getBindingResult().getFieldErrors()
                .forEach(e -> champs.putIfAbsent(e.getField(), e.getDefaultMessage()));
        return ResponseEntity.badRequest()
                .body(new ApiError(HttpStatus.BAD_REQUEST.value(), "Données invalides", champs));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiError> handleJsonIllisible(HttpMessageNotReadableException ex) {
        return reponse(HttpStatus.BAD_REQUEST, "Corps de la requête invalide ou mal formé");
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiError> handleTypeInvalide(MethodArgumentTypeMismatchException ex) {
        return reponse(HttpStatus.BAD_REQUEST, "Valeur invalide pour le paramètre '" + ex.getName() + "'");
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ApiError> handleParamManquant(MissingServletRequestParameterException ex) {
        return reponse(HttpStatus.BAD_REQUEST, "Paramètre obligatoire manquant : '" + ex.getParameterName() + "'");
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ApiError> handleMethodeNonSupportee(HttpRequestMethodNotSupportedException ex) {
        return reponse(HttpStatus.METHOD_NOT_ALLOWED, "Méthode HTTP non supportée sur cette route");
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ApiError> handleRouteInconnue(NoResourceFoundException ex) {
        return reponse(HttpStatus.NOT_FOUND, "Route introuvable");
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiError> handleAccessDenied(AccessDeniedException ex) {
        logger.warn("Accès refusé : {}", ex.getMessage());
        return reponse(HttpStatus.FORBIDDEN, "Accès refusé : droits insuffisants");
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleGenericException(Exception ex) {
        logger.error("Erreur inattendue : {}", ex.getMessage(), ex);
        return reponse(HttpStatus.INTERNAL_SERVER_ERROR, "Une erreur interne est survenue");
    }
}