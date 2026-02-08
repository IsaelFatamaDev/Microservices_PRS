package pe.edu.vallegrande.vgmsorganizations.domain.exceptions.base;

public class ConflictException extends DomainException{
    public ConflictException(String message) {
        super(message, "CONFLICT", 409);
    }
}
