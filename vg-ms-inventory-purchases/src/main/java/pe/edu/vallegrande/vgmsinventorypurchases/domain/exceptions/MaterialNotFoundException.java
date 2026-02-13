package pe.edu.vallegrande.vgmsinventorypurchases.domain.exceptions;

public class MaterialNotFoundException extends NotFoundException {
    public MaterialNotFoundException(String id) {
        super("Material", id);
    }
}
