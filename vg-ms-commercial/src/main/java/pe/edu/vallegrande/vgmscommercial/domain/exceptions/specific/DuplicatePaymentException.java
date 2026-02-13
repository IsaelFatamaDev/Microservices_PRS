package pe.edu.vallegrande.vgmscommercial.domain.exceptions.specific;

import pe.edu.vallegrande.vgmscommercial.domain.exceptions.base.ConflictException;

public class DuplicatePaymentException extends ConflictException {

    public DuplicatePaymentException(String receiptNumber) {
        super(String.format("Payment already exists for receipt '%s'", receiptNumber));
    }
}