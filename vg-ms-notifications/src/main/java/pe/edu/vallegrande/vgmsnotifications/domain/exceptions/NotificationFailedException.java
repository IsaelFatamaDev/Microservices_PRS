package pe.edu.vallegrande.vgmsnotifications.domain.exceptions;

public class NotificationFailedException extends DomainException {

    public NotificationFailedException(String message) {
        super("NOTIFICATION_FAILED", 503, message);
    }

    public NotificationFailedException(String message, Throwable cause) {
        super("NOTIFICATION_FAILED", 503, message, cause);
    }
}
