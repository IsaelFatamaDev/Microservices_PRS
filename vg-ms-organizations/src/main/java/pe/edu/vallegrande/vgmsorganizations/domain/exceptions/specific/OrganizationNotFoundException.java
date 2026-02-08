package pe.edu.vallegrande.vgmsorganizations.domain.exceptions.specific;

import pe.edu.vallegrande.vgmsorganizations.domain.exceptions.base.NotFoundException;

public class OrganizationNotFoundException extends NotFoundException {

    public OrganizationNotFoundException(String id){
        super("Organization", id);
    }
}
