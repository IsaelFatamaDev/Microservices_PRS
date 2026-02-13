package pe.edu.vallegrande.vgmscommercial.domain.exceptions.specific;

import pe.edu.vallegrande.vgmscommercial.domain.exceptions.base.NotFoundException;

public class ReceiptNotFoundException extends NotFoundException {

    public ReceiptNotFoundException(String id) {
        super("Receipt", id);
    }
}
