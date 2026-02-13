package pe.edu.vallegrande.vgmscommercial.infrastructure.rest.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;
import pe.edu.vallegrande.vgmscommercial.application.dto.common.ApiResponse;
import pe.edu.vallegrande.vgmscommercial.domain.exceptions.base.*;

import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

     @ExceptionHandler(NotFoundException.class)
     public ResponseEntity<ApiResponse<Void>> handleNotFound(NotFoundException ex) {
          log.warn("Resource not found: {}", ex.getMessage());
          return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(ex.getMessage()));
     }

     @ExceptionHandler(BusinessRuleException.class)
     public ResponseEntity<ApiResponse<Void>> handleBusinessRule(BusinessRuleException ex) {
          log.warn("Business rule violation: {}", ex.getMessage());
          return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                    .body(ApiResponse.error(ex.getMessage()));
     }

     @ExceptionHandler(ConflictException.class)
     public ResponseEntity<ApiResponse<Void>> handleConflict(ConflictException ex) {
          log.warn("Conflict: {}", ex.getMessage());
          return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(ApiResponse.error(ex.getMessage()));
     }

     @ExceptionHandler(ValidationException.class)
     public ResponseEntity<ApiResponse<Void>> handleValidation(ValidationException ex) {
          log.warn("Validation error: {}", ex.getMessage());
          return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(ex.getMessage()));
     }

     @ExceptionHandler(ExternalServiceException.class)
     public ResponseEntity<ApiResponse<Void>> handleExternalService(ExternalServiceException ex) {
          log.error("External service error: {}", ex.getMessage());
          return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body(ApiResponse.error(ex.getMessage()));
     }

     @ExceptionHandler(DomainException.class)
     public ResponseEntity<ApiResponse<Void>> handleDomain(DomainException ex) {
          log.error("Domain error: {}", ex.getMessage());
          return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(ex.getMessage()));
     }

     @ExceptionHandler(WebExchangeBindException.class)
     public ResponseEntity<ApiResponse<Void>> handleBindException(WebExchangeBindException ex) {
          String errors = ex.getFieldErrors().stream()
                    .map(e -> e.getField() + ": " + e.getDefaultMessage())
                    .collect(Collectors.joining(", "));
          log.warn("Validation errors: {}", errors);
          return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(errors));
     }

     @ExceptionHandler(Exception.class)
     public ResponseEntity<ApiResponse<Void>> handleGeneral(Exception ex) {
          log.error("Unexpected error: {}", ex.getMessage(), ex);
          return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Internal server error"));
     }
}
