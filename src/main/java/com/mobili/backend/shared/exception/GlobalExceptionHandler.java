package com.mobili.backend.shared.exception;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // 1. GESTION DES DOUBLONS (SQL)
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrity(DataIntegrityViolationException ex, WebRequest request) {
        String message = "Cette ressource existe déjà.";
        if (ex.getMessage() != null && ex.getMessage().contains("plate_number")) {
            message = "Un véhicule avec cette plaque est déjà enregistré.";
        }
        return buildErrorResponse(HttpStatus.CONFLICT, "Conflict", message, request);
    }

    // 2. GESTION DES ERREURS DE VALIDATION (@NotBlank, etc.) - Version détaillée
    // pour le Front
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors()
                .forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));

        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.BAD_REQUEST.value());
        body.put("errors", errors); // Le front Angular pourra mapper ces erreurs sous chaque input

        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    // 3. RESSOURCE NON TROUVÉE (404)
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFound(ResourceNotFoundException ex, WebRequest request) {
        return buildErrorResponse(HttpStatus.NOT_FOUND, "Not Found", ex.getMessage(), request);
    }

    // 4. TOUTES LES AUTRES ERREURS (500) - Très important pour ne pas "crash"
    // proprement
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGlobalException(Exception ex, WebRequest request) {
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error", ex.getMessage(), request);
    }

    // Méthode utilitaire pour éviter de répéter le code de création d'ErrorResponse
    private ResponseEntity<ErrorResponse> buildErrorResponse(HttpStatus status, String error, String message,
            WebRequest request) {
        ErrorResponse errorDetails = new ErrorResponse(
                LocalDateTime.now(),
                status.value(),
                error,
                message,
                request.getDescription(false));
        return new ResponseEntity<>(errorDetails, status);
    }
}