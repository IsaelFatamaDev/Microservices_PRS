package pe.edu.vallegrande.vgmsinfrastructure.domain.exceptions;

public class AssignmentNotFoundException extends NotFoundException {
    public AssignmentNotFoundException(String id) {
        super("WaterBoxAssignment", id);
    }
}
