package pe.edu.vallegrande.vgmsinfrastructure.domain.exceptions;

public class WaterBoxAlreadyAssignedException extends ConflictException {
    public WaterBoxAlreadyAssignedException(String waterBoxId) {
        super("Water box with ID '" + waterBoxId + "' is already assigned");
    }
}
