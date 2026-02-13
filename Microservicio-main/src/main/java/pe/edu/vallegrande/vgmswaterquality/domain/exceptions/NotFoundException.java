package pe.edu.vallegrande.vgmswaterquality.domain.exceptions;

public class NotFoundException extends DomainException {
    public NotFoundException(String resource, String id) {
        super(
                String.format("%s con ID '%s' no encontrado", resource, id),
                "RESOURCE_NOT_FOUND",
                404);
    }

    public NotFoundException(String message) {
        super(message, "RESOURCE_NOT_FOUND", 404);
    }
}
