package pe.edu.vallegrande.msdistribution.domain.exceptions.base;

public class NotFoundException extends DomainException {

    public NotFoundException(String message, String errorCode) {
        super(message, errorCode, 404);
    }

    public NotFoundException(String resourceName, String fieldName, String fieldValue) {
        super(
            String.format("%s with %s '%s' not found", resourceName, fieldName, fieldValue),
            resourceName.toUpperCase().replace(" ", "_") + "_NOT_FOUND",
            404
        );
    }
}
