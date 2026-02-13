package pe.edu.vallegrande.vgmsclaims.domain.exceptions.specific;

import pe.edu.vallegrande.vgmsclaims.domain.exceptions.base.NotFoundException;

/**
 * Specific exception when an incident is not found
 */
public class IncidentNotFoundException extends NotFoundException {

    public IncidentNotFoundException(String id) {
        super("Incident", id);
    }

    public IncidentNotFoundException(String field, String value, boolean byField) {
        super(String.format("Incident with %s '%s' not found", field, value));
    }
}
