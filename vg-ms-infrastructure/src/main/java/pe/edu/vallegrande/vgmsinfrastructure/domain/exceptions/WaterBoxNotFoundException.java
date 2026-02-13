package pe.edu.vallegrande.vgmsinfrastructure.domain.exceptions;

public class WaterBoxNotFoundException extends NotFoundException {
    public WaterBoxNotFoundException(String id) {
        super("WaterBox", id);
    }
}
