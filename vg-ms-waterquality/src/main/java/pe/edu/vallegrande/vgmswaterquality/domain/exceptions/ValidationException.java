package pe.edu.vallegrande.vgmswaterquality.domain.exceptions;


import lombok.Getter;

@Getter
public class ValidationException extends DomainException{
    private final String field;

    public ValidationException(String field, String message) {
        super(message, "VALIDATION_ERROR", 400);
        this.field = field;
    }
}