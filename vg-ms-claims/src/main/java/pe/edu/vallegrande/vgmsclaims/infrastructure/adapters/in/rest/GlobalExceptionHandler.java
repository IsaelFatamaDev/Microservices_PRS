package pe.edu.vallegrande.vgmsclaims.infrastructure.adapters.in.rest;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;
import pe.edu.vallegrande.vgmsclaims.application.dto.common.ApiResponse;
import pe.edu.vallegrande.vgmsclaims.application.dto.common.ErrorMessage;
import pe.edu.vallegrande.vgmsclaims.domain.exceptions.base.*;
import pe.edu.vallegrande.vgmsclaims.domain.exceptions.specific.*;

import java.util.stream.Collectors;

/**
 * Global exception handler for the REST API.
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

     @ExceptionHandler(NotFoundException.class)
     public ResponseEntity<ApiResponse<Void>> handleNotFoundException(NotFoundException ex) {
          log.warn("Resource not found: {}", ex.getMessage());
          ErrorMessage error = ErrorMessage.of("NOT_FOUND", ex.getMessage());
          return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(error));
     }

     @ExceptionHandler({ IncidentNotFoundException.class, ComplaintNotFoundException.class })
     public ResponseEntity<ApiResponse<Void>> handleSpecificNotFound(RuntimeException ex) {
          log.warn("Specific resource not found: {}", ex.getMessage());
          ErrorMessage error = ErrorMessage.of("NOT_FOUND", ex.getMessage());
          return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(error));
     }

     @ExceptionHandler(BusinessRuleException.class)
     public ResponseEntity<ApiResponse<Void>> handleBusinessRuleException(BusinessRuleException ex) {
          log.warn("Business rule violation: {}", ex.getMessage());
          ErrorMessage error = ErrorMessage.of("BUSINESS_RULE_VIOLATION", ex.getMessage());
          return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                    .body(ApiResponse.error(error));
     }

     @ExceptionHandler(ConflictException.class)
     public ResponseEntity<ApiResponse<Void>> handleConflictException(ConflictException ex) {
          log.warn("Conflict: {}", ex.getMessage());
          ErrorMessage error = ErrorMessage.of("CONFLICT", ex.getMessage());
          return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(ApiResponse.error(error));
     }

     @ExceptionHandler({ IncidentAlreadyResolvedException.class, ComplaintAlreadyClosedException.class })
     public ResponseEntity<ApiResponse<Void>> handleAlreadyProcessed(RuntimeException ex) {
          log.warn("Resource already processed: {}", ex.getMessage());
          ErrorMessage error = ErrorMessage.of("CONFLICT", ex.getMessage());
          return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(ApiResponse.error(error));
     }

     @ExceptionHandler(InvalidTransitionException.class)
     public ResponseEntity<ApiResponse<Void>> handleInvalidTransition(InvalidTransitionException ex) {
          log.warn("Invalid state transition: {}", ex.getMessage());
          ErrorMessage error = ErrorMessage.of("INVALID_TRANSITION", ex.getMessage());
          return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                    .body(ApiResponse.error(error));
     }

     @ExceptionHandler(UnauthorizedAssignmentException.class)
     public ResponseEntity<ApiResponse<Void>> handleUnauthorizedAssignment(UnauthorizedAssignmentException ex) {
          log.warn("Unauthorized assignment: {}", ex.getMessage());
          ErrorMessage error = ErrorMessage.of("UNAUTHORIZED", ex.getMessage());
          return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.error(error));
     }

     @ExceptionHandler(ValidationException.class)
     public ResponseEntity<ApiResponse<Void>> handleValidationException(ValidationException ex) {
          log.warn("Validation error: {}", ex.getMessage());
          ErrorMessage error = ErrorMessage.of("VALIDATION_ERROR", ex.getMessage());
          return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(error));
     }

     @ExceptionHandler(WebExchangeBindException.class)
     public ResponseEntity<ApiResponse<Void>> handleWebExchangeBindException(WebExchangeBindException ex) {
          log.warn("Validation error in submitted data");
          var fieldErrors = ex.getBindingResult().getFieldErrors().stream()
                    .map(fe -> ErrorMessage.FieldError.builder()
                              .field(fe.getField())
                              .message(fe.getDefaultMessage())
                              .rejectedValue(fe.getRejectedValue())
                              .build())
                    .collect(Collectors.toList());
          ErrorMessage error = ErrorMessage.builder()
                    .code("VALIDATION_ERROR")
                    .message("Validation error in submitted data")
                    .fieldErrors(fieldErrors)
                    .build();
          return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(error));
     }

     @ExceptionHandler(DomainException.class)
     public ResponseEntity<ApiResponse<Void>> handleDomainException(DomainException ex) {
          log.error("Domain error: {}", ex.getMessage());
          ErrorMessage error = ErrorMessage.of("DOMAIN_ERROR", ex.getMessage());
          return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(error));
     }

     @ExceptionHandler(Exception.class)
     public ResponseEntity<ApiResponse<Void>> handleGenericException(Exception ex) {
          log.error("Internal server error: {}", ex.getMessage(), ex);
          ErrorMessage error = ErrorMessage.of("INTERNAL_ERROR", "Internal server error");
          return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(error));
     }
}
