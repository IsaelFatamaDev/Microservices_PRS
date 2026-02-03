package pe.edu.vallegrande.vgmsusers.domain.exceptions;

public class ConflictException extends DomainException{
    public ConflictException(String message) {
        super(message, "RESOURCE_CONFLICT", 409);
    }
}
