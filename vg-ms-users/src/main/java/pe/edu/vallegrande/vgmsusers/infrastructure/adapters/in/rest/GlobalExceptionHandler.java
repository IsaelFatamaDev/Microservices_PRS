package pe.edu.vallegrande.vgmsusers.infrastructure.adapters.in.rest;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import pe.edu.vallegrande.vgmsusers.application.dto.common.ApiResponse;
import pe.edu.vallegrande.vgmsusers.application.dto.common.ErrorMessage;
import pe.edu.vallegrande.vgmsusers.domain.exceptions.DomainException;
import pe.edu.vallegrande.vgmsusers.domain.exceptions.ValidationException;
import reactor.core.publisher.Mono;

import java.util.List;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(DomainException.class)
    public Mono<ResponseEntity<ApiResponse<Void>>> handleDomainException(DomainException ex) {
        log.error("Domain Exception: {} - Code: {}", ex.getMessage(), ex.getErrorCode());
        ErrorMessage error = ErrorMessage.of(
            ex.getMessage(),
            ex.getErrorCode(),
            ex.getHttpStatus()
        );

        return Mono.just(
            ResponseEntity.status(ex.getHttpStatus())
                .body(ApiResponse.error(ex.getMessage(), error))
        );
    }

    @ExceptionHandler(ValidationException.class)
    public Mono<ResponseEntity<ApiResponse<Void>>> handleValidationException(ValidationException ex) {
        log.error("Validation Exception: {} - Field: {}", ex.getMessage(), ex.getField());

        ErrorMessage error = ErrorMessage.validation(
            ex.getField(),
            ex.getMessage(),
            ex.getErrorCode()
        );

        return Mono.just(
            ResponseEntity.status(ex.getHttpStatus())
                .body(ApiResponse.error(ex.getMessage(), error))
        );
    }

    @ExceptionHandler(WebExchangeBindException.class)
    public Mono<ResponseEntity<ApiResponse<Void>>> handleValidationErrors(WebExchangeBindException ex) {
        log.error("Validation Errors: {}", ex.getBindingResult().getErrorCount());

        List<ErrorMessage> errors = ex.getBindingResult()
            .getFieldErrors()
            .stream()
            .map(error -> ErrorMessage.validation(error.getField(), error.getDefaultMessage(), "VALIDATION_ERROR"))
            .toList();

        return Mono.just(ResponseEntity.badRequest().body(ApiResponse.error("Errores de validación", errors)));
    }

    @ExceptionHandler(WebClientResponseException.class)
    public Mono<ResponseEntity<ApiResponse<Void>>> handleWebClientResponseException(WebClientResponseException ex) {
        log.error("External service error: {} - Status: {}", ex.getMessage(), ex.getStatusCode());
        ErrorMessage error = ErrorMessage.of(
            "Error de comunicación con servicio externo",
            "EXTERNAL_SERVICE_ERROR",
            ex.getStatusCode().value()
        );

        return Mono.just(ResponseEntity.status(HttpStatus.BAD_GATEWAY)
            .body(ApiResponse.error("Servicio externo no disponible", error)));
    }

    @ExceptionHandler(Exception.class)
    public Mono<ResponseEntity<ApiResponse<Void>>> handleGenericError(Exception ex) {
        log.error("Unexpected error: {}", ex.getMessage());
        ErrorMessage error = ErrorMessage.of(
            "Error interno del servidor",
            "INTERNAL_SERVER_ERROR",
            500
        );

        return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(ApiResponse.error("Error interno del servidor", error)));
    }

}
