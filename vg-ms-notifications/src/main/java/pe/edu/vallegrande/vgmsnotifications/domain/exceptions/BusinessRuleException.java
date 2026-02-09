package pe.edu.vallegrande.vgmsnotifications.domain.exceptions;

public class BusinessRuleException extends DomainException {

    public BusinessRuleException(String message) {
        super("BUSINESS_RULE_VIOLATION", message);
    }
}
