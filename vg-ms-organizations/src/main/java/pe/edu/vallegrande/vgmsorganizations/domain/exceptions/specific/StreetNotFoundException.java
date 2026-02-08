package pe.edu.vallegrande.vgmsorganizations.domain.exceptions.specific;

import pe.edu.vallegrande.vgmsorganizations.domain.exceptions.base.NotFoundException;

public class StreetNotFoundException extends NotFoundException {
    public StreetNotFoundException(String id){
        super("Street", id);
    }
}
