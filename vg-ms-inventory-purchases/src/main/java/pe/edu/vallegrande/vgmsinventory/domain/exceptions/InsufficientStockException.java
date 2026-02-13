package pe.edu.vallegrande.vgmsinventorypurchases.domain.exceptions;

public class InsufficientStockException extends BusinessRuleException {
    public InsufficientStockException(String materialId, Double requested, Double available) {
        super(String.format("Insufficient stock for material '%s'. Requested: %.2f, Available: %.2f",
                materialId, requested, available), "INSUFFICIENT_STOCK");
    }
}
