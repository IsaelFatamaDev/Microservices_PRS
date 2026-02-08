package pe.edu.vallegrande.vgmsorganizations.domain.exceptions.specific;

import pe.edu.vallegrande.vgmsorganizations.domain.exceptions.base.NotFoundException;

public class FareNotFoundException extends NotFoundException {
    public FareNotFoundException(String id){
        super("Fare", id);
    }
}
