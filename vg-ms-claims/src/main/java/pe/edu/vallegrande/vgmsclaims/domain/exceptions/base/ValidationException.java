package pe.edu.vallegrande.vgmsclaims.domain.exceptions.base;

import java.util.Collections;
import java.util.List;

/**
 * Exception for validation errors
 */
public class ValidationException extends DomainException {
    
    private final List<String> errors;
    
    public ValidationException(String message) {
        super("VALIDATION_ERROR", message);
        this.errors = Collections.singletonList(message);
    }
    
    public ValidationException(List<String> errors) {
        super("VALIDATION_ERROR", String.join(", ", errors));
        this.errors = errors;
    }
    
    public ValidationException(String field, String message) {
        super("VALIDATION_ERROR", String.format("Campo '%s': %s", field, message));
        this.errors = Collections.singletonList(getMessage());
    }
    
    public List<String> getErrors() {
        return errors;
    }
}
