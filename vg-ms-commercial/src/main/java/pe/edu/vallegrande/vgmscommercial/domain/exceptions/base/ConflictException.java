package pe.edu.vallegrande.vgmscommercial.domain.exceptions.base;

public class ConflictException extends DomainException {

    public ConflictException(String message) {
        super(message, "RESOURCE_CONFLICT", 409);
    }
}