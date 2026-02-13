package pe.edu.vallegrande.vgmscommercial.domain.exceptions.specific;

import pe.edu.vallegrande.vgmscommercial.domain.exceptions.base.NotFoundException;

public class ServiceCutNotFoundException extends NotFoundException {

    public ServiceCutNotFoundException(String id) {
        super("ServiceCut", id);
    }
}