package pe.edu.vallegrande.vgmswaterquality.domain.exceptions;

public class QualityTestNotFoundException extends NotFoundException{

    public QualityTestNotFoundException( String id) {
        super("QualityTest",id);
    }
}
