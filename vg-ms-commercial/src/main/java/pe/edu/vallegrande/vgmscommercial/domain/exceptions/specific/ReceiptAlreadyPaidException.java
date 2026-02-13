package pe.edu.vallegrande.vgmscommercial.domain.exceptions.specific;

import pe.edu.vallegrande.vgmscommercial.domain.exceptions.base.BusinessRuleException;

public class ReceiptAlreadyPaidException extends BusinessRuleException {

    public ReceiptAlreadyPaidException(String receiptNumber) {
        super(String.format("Receipt '%s' is already paid", receiptNumber), "RECEIPT_ALREADY_PAID");
    }
}