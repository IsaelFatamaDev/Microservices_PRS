package pe.edu.vallegrande.vgmswaterquality.domain.exceptions;

public class BusinessRuleException extends DomainException{
    public BusinessRuleException(String message) {
        super(message, "BUSINESS_RULE_VIOLATION", 400);
    }
    public BusinessRuleException (String message , String errorCode){
        super(message,errorCode , 400);
    }
}
