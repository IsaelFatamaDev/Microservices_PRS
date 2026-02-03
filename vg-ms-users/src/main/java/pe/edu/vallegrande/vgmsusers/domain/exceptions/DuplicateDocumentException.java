package pe.edu.vallegrande.vgmsusers.domain.exceptions;

public class DuplicateDocumentException extends ConflictException {

    public DuplicateDocumentException(String documentNumber) {
        super(String.format("User with document number '%s' already exists", documentNumber));
    }
}
