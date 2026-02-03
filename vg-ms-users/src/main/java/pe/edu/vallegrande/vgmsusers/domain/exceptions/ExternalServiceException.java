package pe.edu.vallegrande.vgmsusers.domain.exceptions;

public class ExternalServiceException extends DomainException {

    public ExternalServiceException(String serviceName) {
        super(String.format("Service '%s' unavailable", serviceName), "EXTERNAL_SERVICE_UNAVAILABLE", 503);
    }

    public ExternalServiceException(String serviceName, Throwable cause) {
        super(String.format("Error communicating with service '%s': %s", serviceName, cause.getMessage()), "EXTERNAL_SERVICE_ERROR", 503, cause);
    }
}
