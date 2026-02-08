package pe.edu.vallegrande.vgmsorganizations.domain.exceptions.specific;

import pe.edu.vallegrande.vgmsorganizations.domain.exceptions.base.NotFoundException;

public class ParameterNotFoundException extends NotFoundException {
    public ParameterNotFoundException(String id){
        super("Parameter", id);
    }
}
