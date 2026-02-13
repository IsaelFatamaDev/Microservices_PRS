package pe.edu.vallegrande.vgmsinfrastructure.infrastructure.rest;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;
import pe.edu.vallegrande.vgmsinfrastructure.application.dto.common.ApiResponse;
import pe.edu.vallegrande.vgmsinfrastructure.application.dto.common.ErrorMessage;
import pe.edu.vallegrande.vgmsinfrastructure.domain.exceptions.BusinessRuleException;
import pe.edu.vallegrande.vgmsinfrastructure.domain.exceptions.ConflictException;
import pe.edu.vallegrande.vgmsinfrastructure.domain.exceptions.DomainException;
import pe.edu.vallegrande.vgmsinfrastructure.domain.exceptions.NotFoundException;

import java.util.List;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleNotFoundException(NotFoundException ex) {
        log.warn("Resource not found: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error(ex.getErrorCode(), ex.getMessage()));
    }

    @ExceptionHandler(BusinessRuleException.class)
    public ResponseEntity<ApiResponse<Void>> handleBusinessRuleException(BusinessRuleException ex) {
        log.warn("Business rule violation: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(ex.getErrorCode(), ex.getMessage()));
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ApiResponse<Void>> handleConflictException(ConflictException ex) {
        log.warn("Conflict: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ApiResponse.error(ex.getErrorCode(), ex.getMessage()));
    }

    @ExceptionHandler(DomainException.class)
    public ResponseEntity<ApiResponse<Void>> handleDomainException(DomainException ex) {
        log.warn("Domain error: {}", ex.getMessage());
        return ResponseEntity.status(ex.getHttpStatus())
                .body(ApiResponse.error(ex.getErrorCode(), ex.getMessage()));
    }

    @ExceptionHandler(WebExchangeBindException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidationException(WebExchangeBindException ex) {
        log.warn("Validation error: {}", ex.getMessage());
        List<ErrorMessage> errors = ex.getFieldErrors().stream()
                .map(fieldError -> ErrorMessage.builder()
                        .code("VALIDATION_ERROR")
                        .message(fieldError.getDefaultMessage())
                        .field(fieldError.getField())
                        .build())
                .toList();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error("Validation failed", errors));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGenericException(Exception ex) {
        log.error("Unexpected error: {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("INTERNAL_ERROR", "An unexpected error occurred"));
    }
}
