package pe.edu.vallegrande.vgmsusers.domain.exceptions;

public class UserNotFoundException extends NotFoundException {

    public UserNotFoundException(String id) {
        super("User", id);
    }

    public static UserNotFoundException byDocument(String documentNumber) {
        return new UserNotFoundException("document", documentNumber, true);
    }

    public static UserNotFoundException byEmail(String email) {
        return new UserNotFoundException("email", email, true);
    }

    private UserNotFoundException(String field, String value, boolean isCustom) {
        super(String.format("User with %s '%s' not found", field, value));
    }
}
