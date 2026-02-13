package pe.edu.vallegrande.vgmsinfrastructure.domain.exceptions;

public class ConflictException extends DomainException {

    public ConflictException(String message) {
        super(message, "RESOURCE_CONFLICT", 409);
    }

    public ConflictException(String message, String errorCode) {
        super(message, errorCode, 409);
    }
}
