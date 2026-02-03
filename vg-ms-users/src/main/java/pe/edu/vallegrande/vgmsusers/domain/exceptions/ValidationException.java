package pe.edu.vallegrande.vgmsusers.domain.exceptions;

public class ValidationException extends DomainException{

    private final String field;

    public ValidationException(String field, String message){
        super(message, "VALIDATION_ERROR", 400);
        this.field = field;
    }

    public String getField() {
        return field;
    }
}
