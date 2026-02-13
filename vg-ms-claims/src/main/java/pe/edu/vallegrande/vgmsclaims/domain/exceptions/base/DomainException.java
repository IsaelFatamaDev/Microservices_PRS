package pe.edu.vallegrande.vgmsclaims.domain.exceptions.base;

/**
 * Abstract base class for domain exceptions
 */
public abstract class DomainException extends RuntimeException {
    
    private final String code;
    
    protected DomainException(String message) {
        super(message);
        this.code = "DOMAIN_ERROR";
    }
    
    protected DomainException(String code, String message) {
        super(message);
        this.code = code;
    }
    
    protected DomainException(String code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
    }
    
    public String getCode() {
        return code;
    }
}
