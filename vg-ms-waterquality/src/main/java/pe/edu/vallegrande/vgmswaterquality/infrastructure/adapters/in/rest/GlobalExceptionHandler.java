package pe.edu.vallegrande.vgmswaterquality.infrastructure.adapters.in.rest;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;
import pe.edu.vallegrande.vgmswaterquality.application.dto.common.ApiResponse;
import pe.edu.vallegrande.vgmswaterquality.application.dto.common.ErrorMessage;
import pe.edu.vallegrande.vgmswaterquality.domain.exceptions.*;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

        @ExceptionHandler(NotFoundException.class)
        public Mono<ResponseEntity<ApiResponse<Void>>> handleNotFoundException(NotFoundException ex) {
                log.warn("Recurso no encontrado: {}", ex.getMessage());
                return Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND)
                                .body(ApiResponse.error(ex.getMessage(),
                                                ErrorMessage.of(ex.getMessage(), ex.getErrorCode(), 404))));
        }

        @ExceptionHandler(ConflictException.class)
        public Mono<ResponseEntity<ApiResponse<Void>>> handleConflictException(ConflictException ex) {
                log.warn("Conflicto: {}", ex.getMessage());
                return Mono.just(ResponseEntity.status(HttpStatus.CONFLICT)
                                .body(ApiResponse.error(ex.getMessage(),
                                                ErrorMessage.of(ex.getMessage(), ex.getErrorCode(), 409))));
        }

        @ExceptionHandler(BusinessRuleException.class)
        public Mono<ResponseEntity<ApiResponse<Void>>> handleBusinessRuleException(BusinessRuleException ex) {
                log.warn("Violación de regla de negocio: {}", ex.getMessage());
                return Mono.just(ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                                .body(ApiResponse.error(ex.getMessage(),
                                                ErrorMessage.of(ex.getMessage(), ex.getErrorCode(), 422))));
        }

        @ExceptionHandler(ValidationException.class)
        public Mono<ResponseEntity<ApiResponse<Void>>> handleValidationException(ValidationException ex) {
                log.warn("Error de validación: {} - Campo: {}", ex.getMessage(), ex.getField());
                return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                .body(ApiResponse.error(ex.getMessage(),
                                                ErrorMessage.validation(ex.getField(), ex.getMessage(),
                                                                ex.getErrorCode()))));
        }

        @ExceptionHandler(WebExchangeBindException.class)
        public Mono<ResponseEntity<ApiResponse<Void>>> handleBindException(WebExchangeBindException ex) {
                log.warn("Errores de validación: {}", ex.getErrorCount());
                List<ErrorMessage> errors = ex.getFieldErrors().stream()
                                .map(fe -> ErrorMessage.validation(
                                                fe.getField(),
                                                fe.getDefaultMessage(),
                                                "VALIDATION_ERROR"))
                                .collect(Collectors.toList());
                return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                .body(ApiResponse.error("Errores de validación", errors)));
        }

        @ExceptionHandler(DomainException.class)
        public Mono<ResponseEntity<ApiResponse<Void>>> handleDomainException(DomainException ex) {
                log.error("Error de dominio: {}", ex.getMessage());
                HttpStatus status = ex.getHttpStatus() != null
                                ? HttpStatus.valueOf(ex.getHttpStatus())
                                : HttpStatus.INTERNAL_SERVER_ERROR;
                return Mono.just(ResponseEntity.status(status)
                                .body(ApiResponse.error(ex.getMessage(),
                                                ErrorMessage.of(ex.getMessage(), ex.getErrorCode(), status.value()))));
        }

        @ExceptionHandler(Exception.class)
        public Mono<ResponseEntity<ApiResponse<Void>>> handleGenericException(Exception ex) {
                log.error("Error inesperado: {}", ex.getMessage(), ex);
                return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body(ApiResponse.error("Error interno del servidor",
                                                ErrorMessage.of("Ha ocurrido un error inesperado", "INTERNAL_ERROR",
                                                                500))));
        }
}
