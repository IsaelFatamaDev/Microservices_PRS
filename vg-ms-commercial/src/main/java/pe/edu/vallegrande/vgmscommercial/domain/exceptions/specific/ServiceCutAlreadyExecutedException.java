package pe.edu.vallegrande.vgmscommercial.domain.exceptions.specific;

import pe.edu.vallegrande.vgmscommercial.domain.exceptions.base.BusinessRuleException;

public class ServiceCutAlreadyExecutedException extends BusinessRuleException {

    public ServiceCutAlreadyExecutedException(String id) {
        super(String.format("Service cut '%s' has already been executed", id), "SERVICE_CUT_ALREADY_EXECUTED");
    }
}