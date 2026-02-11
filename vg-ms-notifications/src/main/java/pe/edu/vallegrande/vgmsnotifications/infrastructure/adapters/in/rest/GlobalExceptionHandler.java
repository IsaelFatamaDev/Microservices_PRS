package pe.edu.vallegrande.vgmsnotifications.infrastructure.adapters.in.rest;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;

import pe.edu.vallegrande.vgmsnotifications.application.dto.common.ApiResponse;
import pe.edu.vallegrande.vgmsnotifications.application.dto.common.ErrorMessage;
import pe.edu.vallegrande.vgmsnotifications.domain.exceptions.*;

import reactor.core.publisher.Mono;

import java.util.List;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NotFoundException.class)
    public Mono<ResponseEntity<ApiResponse<Void>>> handleNotFound(NotFoundException e) {
        log.warn("Not Found: {}", e.getMessage());
        return Mono.just(ResponseEntity.status(e.getHttpStatus())
                .body(ApiResponse.error(e.getMessage())));
    }

    @ExceptionHandler(BusinessRuleException.class)
    public Mono<ResponseEntity<ApiResponse<Void>>> handleBusinessRule(BusinessRuleException e) {
        log.warn("Business Rule: {}", e.getMessage());
        return Mono.just(ResponseEntity.status(e.getHttpStatus())
                .body(ApiResponse.error(e.getMessage())));
    }

    @ExceptionHandler(InvalidRecipientException.class)
    public Mono<ResponseEntity<ApiResponse<Void>>> handleInvalidRecipient(InvalidRecipientException e) {
        log.warn("Invalid Recipient: {}", e.getMessage());
        return Mono.just(ResponseEntity.status(e.getHttpStatus())
                .body(ApiResponse.error(e.getMessage())));
    }

    @ExceptionHandler(TemplateNotFoundException.class)
    public Mono<ResponseEntity<ApiResponse<Void>>> handleTemplateNotFound(TemplateNotFoundException e) {
        log.error("Template Error: {}", e.getMessage());
        return Mono.just(ResponseEntity.status(e.getHttpStatus())
                .body(ApiResponse.error(e.getMessage())));
    }

    @ExceptionHandler(NotificationFailedException.class)
    public Mono<ResponseEntity<ApiResponse<Void>>> handleNotificationFailed(NotificationFailedException e) {
        log.error("Notification failed: {}", e.getMessage());
        return Mono.just(ResponseEntity.status(e.getHttpStatus())
                .body(ApiResponse.error("Notification delivery failed: " + e.getMessage())));
    }

    @ExceptionHandler(WebExchangeBindException.class)
    public Mono<ResponseEntity<ApiResponse<Void>>> handleValidation(WebExchangeBindException e) {
        List<ErrorMessage> errors = e.getFieldErrors().stream()
                .map(f -> ErrorMessage.validation(f.getField(), f.getDefaultMessage(), f.getCode()))
                .toList();
        return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error("Validation failed", errors)));
    }

    @ExceptionHandler(Exception.class)
    public Mono<ResponseEntity<ApiResponse<Void>>> handleGeneric(Exception e) {
        log.error("Unexpected error: ", e);
        return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Internal server error")));
    }
}
