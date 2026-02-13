package pe.edu.vallegrande.vgmsclaims.domain.exceptions.base;

/**
 * Exception for resource conflicts
 */
public class ConflictException extends DomainException {
    
    public ConflictException(String message) {
        super("CONFLICT", message);
    }
    
    public ConflictException(String resourceType, String identifier) {
        super("CONFLICT", String.format("%s with identifier '%s' already exists or is in conflict", resourceType, identifier));
    }
}
