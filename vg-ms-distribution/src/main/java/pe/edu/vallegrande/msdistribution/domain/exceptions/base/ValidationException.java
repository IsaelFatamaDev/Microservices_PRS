package pe.edu.vallegrande.msdistribution.domain.exceptions.base;

public class ValidationException extends DomainException {

    private final String field;

    public ValidationException(String message, String errorCode) {
        super(message, errorCode, 400);
        this.field = null;
    }

    public ValidationException(String message, String errorCode, String field) {
        super(message, errorCode, 400);
        this.field = field;
    }

    public String getField() {
        return field;
    }
}
