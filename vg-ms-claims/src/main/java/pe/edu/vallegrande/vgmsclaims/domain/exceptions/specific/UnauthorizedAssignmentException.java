package pe.edu.vallegrande.vgmsclaims.domain.exceptions.specific;

import pe.edu.vallegrande.vgmsclaims.domain.exceptions.base.BusinessRuleException;

/**
 * Exception when a user does not have permissions to assign a resource
 */
public class UnauthorizedAssignmentException extends BusinessRuleException {
    
    public UnauthorizedAssignmentException(String userId, String resourceType) {
        super("UNAUTHORIZED_ASSIGNMENT", 
              String.format("User '%s' does not have permissions to assign %s", userId, resourceType));
    }
    
    public UnauthorizedAssignmentException(String userId, String resourceType, String resourceId) {
        super("UNAUTHORIZED_ASSIGNMENT", 
              String.format("User '%s' does not have permissions to assign %s with id '%s'", 
                           userId, resourceType, resourceId));
    }
}
