package pe.edu.vallegrande.vgmscommercial.domain.exceptions.base;

public class ExternalServiceException extends DomainException {

    public ExternalServiceException(String serviceName) {
        super(
            String.format("Service '%s' is temporarily unavailable", serviceName),
            "EXTERNAL_SERVICE_UNAVAILABLE",
            503
        );
    }

    public ExternalServiceException(String serviceName, Throwable cause) {
        super(
            String.format("Error communicating with service '%s'", serviceName),
            "EXTERNAL_SERVICE_ERROR",
            503,
            cause
        );
    }
}