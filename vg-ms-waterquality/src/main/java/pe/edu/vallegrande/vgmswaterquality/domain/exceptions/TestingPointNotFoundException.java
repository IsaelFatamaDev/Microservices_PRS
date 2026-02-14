package pe.edu.vallegrande.vgmswaterquality.domain.exceptions;

public class TestingPointNotFoundException extends NotFoundException{

    public TestingPointNotFoundException(String id) {
        super("TestingPoint", id);
    }
}
