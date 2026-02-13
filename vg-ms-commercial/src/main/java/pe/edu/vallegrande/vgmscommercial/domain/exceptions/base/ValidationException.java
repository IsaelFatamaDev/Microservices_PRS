package pe.edu.vallegrande.vgmscommercial.domain.exceptions.base;

public class ValidationException extends DomainException {

    public ValidationException(String message) {
        super(message, "VALIDATION_ERROR", 400);
    }

    public ValidationException(String field, String message) {
        super(String.format("Field '%s': %s", field, message), "VALIDATION_ERROR", 400);
    }
}