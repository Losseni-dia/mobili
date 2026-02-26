package com.mobili.backend.shared.mobiliError;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import com.mobili.backend.shared.mobiliError.exception.ErrorDetails;
import com.mobili.backend.shared.mobiliError.exception.MobiliErrorCode;
import com.mobili.backend.shared.mobiliError.exception.MobiliException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // 1. GESTION DES ERREURS PERSONNALISÉES
    @ExceptionHandler(MobiliException.class)
    public ResponseEntity<ErrorDetails> handleMobiliException(MobiliException ex, WebRequest request) {
        return buildResponse(ex.getErrorCode(), ex.getMessage(), request);
    }

    // 2. GESTION DES DOUBLONS SQL
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorDetails> handleDataIntegrity(DataIntegrityViolationException ex, WebRequest request) {
        String message = "Cette ressource existe déjà.";
        if (ex.getMessage() != null && ex.getMessage().contains("plate_number")) {
            message = "Un véhicule avec cette plaque est déjà enregistré.";
        }
        return buildResponse(MobiliErrorCode.DUPLICATE_RESOURCE, message, request);
    }

    // 3. GESTION DES ERREURS DE VALIDATION (@Valid)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> fieldErrors = new HashMap<>();
        ex.getBindingResult().getFieldErrors()
                .forEach(error -> fieldErrors.put(error.getField(), error.getDefaultMessage()));

        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", MobiliErrorCode.VALIDATION_ERROR.getStatus().value());
        body.put("errorCode", MobiliErrorCode.VALIDATION_ERROR.getCode());
        body.put("errors", fieldErrors);

        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    // 4. FALLBACK (Erreurs imprévues)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorDetails> handleGlobal(Exception ex, WebRequest request) {
        return buildResponse(MobiliErrorCode.INTERNAL_SERVER_ERROR, ex.getMessage(), request);
    }

    // Méthode utilitaire privée utilisant le Record ErrorDetails
    private ResponseEntity<ErrorDetails> buildResponse(MobiliErrorCode code, String message, WebRequest request) {
        ErrorDetails details = new ErrorDetails(
                LocalDateTime.now(),
                code.getStatus().value(),
                code.getCode(),
                message,
                request.getDescription(false).replace("uri=", ""));
        return new ResponseEntity<>(details, code.getStatus());
    }
}