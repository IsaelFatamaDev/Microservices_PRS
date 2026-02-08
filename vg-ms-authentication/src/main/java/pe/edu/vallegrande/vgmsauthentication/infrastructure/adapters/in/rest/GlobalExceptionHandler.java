package pe.edu.vallegrande.vgmsauthentication.infrastructure.adapters.in.rest;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;
import pe.edu.vallegrande.vgmsauthentication.domain.exceptions.*;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DomainException.class)
    public Mono<ResponseEntity<Map<String, Object>>> handleDomainException(DomainException ex) {
        log.warn("Domain exception: {} - {}", ex.getErrorCode(), ex.getMessage());

        return Mono.just(ResponseEntity
            .status(ex.getHttpStatus())
            .body(buildErrorResponse(ex.getErrorCode(), ex.getMessage(), ex.getHttpStatus())));
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public Mono<ResponseEntity<Map<String, Object>>> handleInvalidCredentials(InvalidCredentialsException ex) {
        log.warn("Authentication failed: {}", ex.getMessage());

        return Mono.just(ResponseEntity
            .status(HttpStatus.UNAUTHORIZED)
            .body(buildErrorResponse(ex.getErrorCode(), ex.getMessage(), 401)));
    }

    @ExceptionHandler(TokenExpiredException.class)
    public Mono<ResponseEntity<Map<String, Object>>> handleTokenExpired(TokenExpiredException ex) {
        log.debug("Token expired: {}", ex.getMessage());

        return Mono.just(ResponseEntity
            .status(HttpStatus.UNAUTHORIZED)
            .body(buildErrorResponse(ex.getErrorCode(), ex.getMessage(), 401)));
    }

    @ExceptionHandler(TokenInvalidException.class)
    public Mono<ResponseEntity<Map<String, Object>>> handleTokenInvalid(TokenInvalidException ex) {
        log.debug("Invalid token: {}", ex.getMessage());

        return Mono.just(ResponseEntity
            .status(HttpStatus.UNAUTHORIZED)
            .body(buildErrorResponse(ex.getErrorCode(), ex.getMessage(), 401)));
    }

    @ExceptionHandler(KeycloakException.class)
    public Mono<ResponseEntity<Map<String, Object>>> handleKeycloakException(KeycloakException ex) {
        log.error("Keycloak error: {}", ex.getMessage(), ex);

        return Mono.just(ResponseEntity
            .status(HttpStatus.SERVICE_UNAVAILABLE)
            .body(buildErrorResponse(ex.getErrorCode(), ex.getMessage(), 503)));
    }

    @ExceptionHandler(WebExchangeBindException.class)
    public Mono<ResponseEntity<Map<String, Object>>> handleValidation(WebExchangeBindException ex){
        String errors = ex.getFieldErrors().stream()
            .map(error -> error.getField() + ": " + error.getDefaultMessage())
            .collect(Collectors.joining());

        log.warn("Validation error: {}", errors);

        return Mono.just(ResponseEntity.badRequest().body(buildErrorResponse("VALIDATION_ERROR", errors, 400)));
    }

    public Mono<ResponseEntity<Map<String,Object>>> handleGenericException(Exception ex){
        log.error("Unexpected error: {}", ex.getMessage(), ex);
        return Mono.just(ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(buildErrorResponse("INTERNAL_ERROR", "An unexpected error ocurred", 500))
        );
    }

    private Map<String, Object> buildErrorResponse(String code, String message, int status) {
        Map<String, Object> error = new HashMap<>();

        error.put("timestamp", LocalDateTime.now().toString());
        error.put("status", status);
        error.put("error", code);
        error.put("message", message);

        return error;
    }
}
