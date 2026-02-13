package pe.edu.vallegrande.vgmsclaims.domain.exceptions.specific;

import pe.edu.vallegrande.vgmsclaims.domain.exceptions.base.BusinessRuleException;

/**
 * Exception when attempting to modify an incident that is already resolved
 */
public class IncidentAlreadyResolvedException extends BusinessRuleException {
    
    public IncidentAlreadyResolvedException(String incidentId) {
        super("INCIDENT_ALREADY_RESOLVED", 
              String.format("Incident with id '%s' is already resolved and cannot be modified", incidentId));
    }
}
