package pe.edu.vallegrande.vgmsclaims.domain.exceptions.specific;

import pe.edu.vallegrande.vgmsclaims.domain.exceptions.base.BusinessRuleException;

/**
 * Exception when attempting to modify a complaint that is already closed
 */
public class ComplaintAlreadyClosedException extends BusinessRuleException {
    
    public ComplaintAlreadyClosedException(String complaintId) {
        super("COMPLAINT_ALREADY_CLOSED", 
              String.format("Complaint with id '%s' is already closed and cannot be modified", complaintId));
    }
}
