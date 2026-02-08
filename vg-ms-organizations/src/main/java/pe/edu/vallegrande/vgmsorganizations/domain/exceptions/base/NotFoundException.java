package pe.edu.vallegrande.vgmsorganizations.domain.exceptions.base;

public class NotFoundException extends DomainException{

    public NotFoundException(String resource, String id){
        super(
            String.format("%s with id '%s' not found", resource, id),
            "NOT_FOUND",
            404
        );
    }
}
