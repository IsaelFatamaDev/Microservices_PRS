package pe.edu.vallegrande.vgmsinfrastructure.domain.exceptions;

public class WaterBoxAlreadySuspendedException extends BusinessRuleException {
    public WaterBoxAlreadySuspendedException(String waterBoxId) {
        super("Water box with ID '" + waterBoxId + "' is already suspended");
    }
}
