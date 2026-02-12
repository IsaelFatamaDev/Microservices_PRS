package pe.edu.vallegrande.msdistribution.domain.exceptions.base;

public class ConflictException extends DomainException {

    public ConflictException(String message, String errorCode) {
        super(message, errorCode, 409);
    }
}
