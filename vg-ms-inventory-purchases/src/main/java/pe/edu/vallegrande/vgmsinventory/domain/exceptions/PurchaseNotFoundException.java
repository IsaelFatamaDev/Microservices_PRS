package pe.edu.vallegrande.vgmsinventorypurchases.domain.exceptions;

public class PurchaseNotFoundException extends NotFoundException {
    public PurchaseNotFoundException(String id) {
        super("Purchase", id);
    }
}
