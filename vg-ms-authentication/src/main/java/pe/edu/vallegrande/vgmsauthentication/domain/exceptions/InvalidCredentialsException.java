package pe.edu.vallegrande.vgmsauthentication.domain.exceptions;

public class InvalidCredentialsException extends DomainException{

    public InvalidCredentialsException(String message) {
        super(message, "INVALID_CREDENTIALS", 401);
    }

    public InvalidCredentialsException(){
        super("Invalid username or password", "INVALID_CREDENTIALS", 401);
    }

    public static InvalidCredentialsException userDisabled(){
        return new InvalidCredentialsException("User account is disabled");
    }

    public static InvalidCredentialsException accountLocked(){
        return new InvalidCredentialsException("Account is locked due to too many failed attempts");
    }
}
