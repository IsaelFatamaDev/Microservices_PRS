package pe.edu.vallegrande.vgmscommercial.domain.exceptions.specific;

import pe.edu.vallegrande.vgmscommercial.domain.exceptions.base.NotFoundException;

public class DebtNotFoundException extends NotFoundException {

    public DebtNotFoundException(String id) {
        super("Debt", id);
    }
}