package pe.edu.vallegrande.vgmsclaims.domain.exceptions.specific;

import pe.edu.vallegrande.vgmsclaims.domain.exceptions.base.NotFoundException;

/**
 * Specific exception when a complaint is not found
 */
public class ComplaintNotFoundException extends NotFoundException {

    public ComplaintNotFoundException(String id) {
        super("Complaint", id);
    }

    public ComplaintNotFoundException(String field, String value, boolean byField) {
        super(String.format("Complaint with %s '%s' not found", field, value));
    }
}
