package pe.edu.vallegrande.vgmsusers.domain.exceptions;

public class InvalidContactException extends BusinessRuleException{

    public InvalidContactException(String message){
        super(message, "INVALID_CONTACT");
    }
}
