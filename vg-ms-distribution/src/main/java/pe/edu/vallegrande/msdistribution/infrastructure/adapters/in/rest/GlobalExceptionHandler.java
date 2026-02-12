package pe.edu.vallegrande.msdistribution.infrastructure.adapters.in.rest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;
import pe.edu.vallegrande.msdistribution.application.dto.common.ApiResponse;
import pe.edu.vallegrande.msdistribution.application.dto.common.ErrorMessage;
import pe.edu.vallegrande.msdistribution.domain.exceptions.base.*;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NotFoundException.class)
    public Mono<ResponseEntity<ApiResponse<Void>>> handleNotFound(NotFoundException ex) {
        return Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error(ex.getMessage(), List.of(
                        ErrorMessage.builder()
                                .code(ex.getErrorCode())
                                .message(ex.getMessage())
                                .build()
                ))));
    }

    @ExceptionHandler(ConflictException.class)
    public Mono<ResponseEntity<ApiResponse<Void>>> handleConflict(ConflictException ex) {
        return Mono.just(ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ApiResponse.error(ex.getMessage(), List.of(
                        ErrorMessage.builder()
                                .code(ex.getErrorCode())
                                .message(ex.getMessage())
                                .build()
                ))));
    }

    @ExceptionHandler(BusinessRuleException.class)
    public Mono<ResponseEntity<ApiResponse<Void>>> handleBusinessRule(BusinessRuleException ex) {
        return Mono.just(ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                .body(ApiResponse.error(ex.getMessage(), List.of(
                        ErrorMessage.builder()
                                .code(ex.getErrorCode())
                                .message(ex.getMessage())
                                .build()
                ))));
    }

    @ExceptionHandler(ValidationException.class)
    public Mono<ResponseEntity<ApiResponse<Void>>> handleValidation(ValidationException ex) {
        return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(ex.getMessage(), List.of(
                        ErrorMessage.builder()
                                .code(ex.getErrorCode())
                                .message(ex.getMessage())
                                .field(ex.getField())
                                .build()
                ))));
    }

    @ExceptionHandler(DomainException.class)
    public Mono<ResponseEntity<ApiResponse<Void>>> handleDomain(DomainException ex) {
        HttpStatus status = HttpStatus.resolve(ex.getHttpStatus()) != null
                ? HttpStatus.resolve(ex.getHttpStatus())
                : HttpStatus.INTERNAL_SERVER_ERROR;
        return Mono.just(ResponseEntity.status(status)
                .body(ApiResponse.error(ex.getMessage())));
    }

    @ExceptionHandler(WebExchangeBindException.class)
    public Mono<ResponseEntity<ApiResponse<Void>>> handleBindException(WebExchangeBindException ex) {
        List<ErrorMessage> errors = ex.getFieldErrors().stream()
                .map(fe -> ErrorMessage.builder()
                        .code("VALIDATION_ERROR")
                        .field(fe.getField())
                        .message(fe.getDefaultMessage())
                        .build())
                .collect(Collectors.toList());
        return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error("Validation failed", errors)));
    }

    @ExceptionHandler(Exception.class)
    public Mono<ResponseEntity<ApiResponse<Void>>> handleGeneral(Exception ex) {
        return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Internal server error: " + ex.getMessage())));
    }
}
