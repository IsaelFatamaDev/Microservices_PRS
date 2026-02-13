package pe.edu.vallegrande.vgmsclaims.domain.exceptions.base;

/**
 * Exception for resources not found
 */
public class NotFoundException extends DomainException {
    
    public NotFoundException(String message) {
        super("NOT_FOUND", message);
    }
    
    public NotFoundException(String resourceType, String id) {
        super("NOT_FOUND", String.format("%s with id '%s' not found", resourceType, id));
    }
}
