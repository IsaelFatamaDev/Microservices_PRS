package pe.edu.vallegrande.vgmscommercial.domain.exceptions.specific;

import pe.edu.vallegrande.vgmscommercial.domain.exceptions.base.NotFoundException;

public class PaymentNotFoundException extends NotFoundException {

    public PaymentNotFoundException(String id) {
        super("Payment", id);
    }
}