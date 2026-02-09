package pe.edu.vallegrande.vgmsorganizations.domain.exceptions.base;

public class BusinessRuleException extends DomainException {
    public BusinessRuleException(String message) {
        super(message, "BUSINESS_RULE_VIOLATION", 400);
    }
}
