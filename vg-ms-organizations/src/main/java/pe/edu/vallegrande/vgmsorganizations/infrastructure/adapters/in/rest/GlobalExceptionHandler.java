package pe.edu.vallegrande.vgmsorganizations.infrastructure.adapters.in.rest;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;
import pe.edu.vallegrande.vgmsorganizations.application.dto.common.ApiResponse;
import pe.edu.vallegrande.vgmsorganizations.application.dto.common.ErrorMessage;
import pe.edu.vallegrande.vgmsorganizations.domain.exceptions.base.*;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleNotFoundException(NotFoundException ex) {
        log.warn("Not found: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(ApiResponse.error(ex.getMessage(),
                ErrorMessage.of(ex.getMessage(), ex.getErrorCode(), 404)));
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ApiResponse<Void>> handleConflictException(ConflictException ex) {
        log.warn("Conflict: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT)
            .body(ApiResponse.error(ex.getMessage(),
                ErrorMessage.of(ex.getMessage(), ex.getErrorCode(), 409)));
    }

    @ExceptionHandler(BusinessRuleException.class)
    public ResponseEntity<ApiResponse<Void>> handleBusinessRuleException(BusinessRuleException ex) {
        log.warn("Business rule violation: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
            .body(ApiResponse.error(ex.getMessage(),
                ErrorMessage.of(ex.getMessage(), ex.getErrorCode(), 422)));
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidationException(ValidationException ex) {
        log.warn("Validation error: {} - Field: {}", ex.getMessage(), ex.getField());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(ApiResponse.error(ex.getMessage(),
                ErrorMessage.validation(ex.getField(), ex.getMessage(), ex.getErrorCode())));
    }

    @ExceptionHandler(WebExchangeBindException.class)
    public ResponseEntity<ApiResponse<Void>> handleBindException(WebExchangeBindException ex) {
        log.warn("Validation errors: {}", ex.getErrorCount());
        List<ErrorMessage> errors = ex.getFieldErrors().stream()
            .map(fe -> ErrorMessage.validation(
                fe.getField(),
                fe.getDefaultMessage(),
                "VALIDATION_ERROR"
            ))
            .collect(Collectors.toList());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(ApiResponse.error("Validation errors", errors));
    }

    @ExceptionHandler(DomainException.class)
    public ResponseEntity<ApiResponse<Void>> handleDomainException(DomainException ex) {
        log.error("Domain error: {}", ex.getMessage());
        HttpStatus status = ex.getHttpStatus() != null
            ? HttpStatus.valueOf(ex.getHttpStatus())
            : HttpStatus.INTERNAL_SERVER_ERROR;
        return ResponseEntity.status(status)
            .body(ApiResponse.error(ex.getMessage(),
                ErrorMessage.of(ex.getMessage(), ex.getErrorCode(), status.value())));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGenericException(Exception ex) {
        log.error("Unexpected error: {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(ApiResponse.error("Internal server error",
                ErrorMessage.of("An unexpected error has occurred", "INTERNAL_ERROR", 500)));
    }
}
