package pe.edu.vallegrande.vgmscommercial.domain.exceptions.base;

public class NotFoundException extends DomainException {

    public NotFoundException(String resource, String id) {
        super(
            String.format("%s with ID '%s' not found", resource, id),
            "RESOURCE_NOT_FOUND",
            404
        );
    }

    public NotFoundException(String message) {
        super(message, "RESOURCE_NOT_FOUND", 404);
    }
}