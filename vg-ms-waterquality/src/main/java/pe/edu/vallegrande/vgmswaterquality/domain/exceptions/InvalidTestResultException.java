package pe.edu.vallegrande.vgmswaterquality.domain.exceptions;

public class InvalidTestResultException extends BusinessRuleException{

    public InvalidTestResultException(String message) {
        super(message , "BUSINESS_RULE_VIOLATION");
    }
}
