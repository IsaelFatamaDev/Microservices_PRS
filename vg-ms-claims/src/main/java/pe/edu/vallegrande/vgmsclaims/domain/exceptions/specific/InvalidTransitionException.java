package pe.edu.vallegrande.vgmsclaims.domain.exceptions.specific;

import pe.edu.vallegrande.vgmsclaims.domain.exceptions.base.BusinessRuleException;

/**
 * Exception when an invalid state transition is attempted
 */
public class InvalidTransitionException extends BusinessRuleException {

    public InvalidTransitionException(String currentState, String targetState) {
        super("INVALID_TRANSITION",
                String.format("Cannot transition from state '%s' to state '%s'", currentState, targetState));
    }

    public InvalidTransitionException(String entityType, String currentState, String targetState) {
        super("INVALID_TRANSITION",
                String.format("The %s cannot transition from state '%s' to state '%s'",
                        entityType, currentState, targetState));
    }
}
