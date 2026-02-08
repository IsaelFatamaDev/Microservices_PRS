package pe.edu.vallegrande.vgmsorganizations.domain.exceptions.specific;

import pe.edu.vallegrande.vgmsorganizations.domain.exceptions.base.NotFoundException;

public class ZoneNotFoundException extends NotFoundException {
    public ZoneNotFoundException(String id) {
        super("Zone", id);
    }
}
