package pe.edu.vallegrande.msdistribution.domain.exceptions.base;

public class BusinessRuleException extends DomainException {

    public BusinessRuleException(String message, String errorCode) {
        super(message, errorCode, 422);
    }
}
