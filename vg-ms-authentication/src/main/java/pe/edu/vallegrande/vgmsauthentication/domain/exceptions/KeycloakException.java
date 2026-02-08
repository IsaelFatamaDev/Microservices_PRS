package pe.edu.vallegrande.vgmsauthentication.domain.exceptions;

public class KeycloakException extends DomainException {
    public KeycloakException(String message) {
        super(message, "KEYCLOAK_ERROR", 503);
    }

    public KeycloakException(String message, Throwable cause) {
        super(message, cause, "KEYCLOAK_ERROR", 503);
    }

    public static KeycloakException connectionError() {
        return new KeycloakException("Error connecting to Keycloak");
    }

    public static KeycloakException configurationError(String detail) {
        return new KeycloakException("Keycloak configuration error: " + detail);
    }

    public static KeycloakException userCreationFailed(String reason) {
        return new KeycloakException("Failed to create user in keycloak: " + reason);
    }
}
