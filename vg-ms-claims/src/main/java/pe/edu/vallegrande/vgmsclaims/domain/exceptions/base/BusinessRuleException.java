package pe.edu.vallegrande.vgmsclaims.domain.exceptions.base;

/**
 * Exception for business rule violations
 */
public class BusinessRuleException extends DomainException {
    
    public BusinessRuleException(String message) {
        super("BUSINESS_RULE_VIOLATION", message);
    }
    
    public BusinessRuleException(String code, String message) {
        super(code, message);
    }
}
