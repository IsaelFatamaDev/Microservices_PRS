package pe.edu.vallegrande.vgmscommercial.domain.exceptions.specific;

import pe.edu.vallegrande.vgmscommercial.domain.exceptions.base.NotFoundException;

public class PettyCashNotFoundException extends NotFoundException {

    public PettyCashNotFoundException(String id) {
        super("PettyCash", id);
    }
}