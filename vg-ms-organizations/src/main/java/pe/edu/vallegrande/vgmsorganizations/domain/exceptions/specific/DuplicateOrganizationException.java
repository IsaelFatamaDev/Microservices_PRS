package pe.edu.vallegrande.vgmsorganizations.domain.exceptions.specific;

import pe.edu.vallegrande.vgmsorganizations.domain.exceptions.base.ConflictException;

public class DuplicateOrganizationException extends ConflictException {
    public DuplicateOrganizationException(String organizationName) {
        super(String.format("Organization with name '%s' already exists", organizationName));
    }
}
