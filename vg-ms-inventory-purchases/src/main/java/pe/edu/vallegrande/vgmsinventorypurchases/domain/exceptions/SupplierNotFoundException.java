package pe.edu.vallegrande.vgmsinventorypurchases.domain.exceptions;

public class SupplierNotFoundException extends NotFoundException {
    public SupplierNotFoundException(String id) {
        super("Supplier", id);
    }
}
