package pe.edu.vallegrande.vgmsnotifications.domain.exceptions;

public class InvalidRecipientException extends DomainException{

    public InvalidRecipientException(String message){
        super("INVALID_RECIPIENT", message);
    }
}
