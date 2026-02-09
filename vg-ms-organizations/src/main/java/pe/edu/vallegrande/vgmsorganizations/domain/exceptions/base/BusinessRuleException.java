package pe.edu.vallegrande.vgmsorganizations.domain.exceptions.base;

public class BussinessRuleException extends DomainException{
    public BussinessRuleException(String message) {
        super(message, "BUSINESS_RULE_VIOLATION", 400);
    }
}
