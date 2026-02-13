package pe.edu.vallegrande.vgmsinfrastructure.domain.exceptions;

public class TransferNotAllowedException extends BusinessRuleException {
    public TransferNotAllowedException(String reason) {
        super("Transfer not allowed: " + reason, "TRANSFER_NOT_ALLOWED");
    }
}
