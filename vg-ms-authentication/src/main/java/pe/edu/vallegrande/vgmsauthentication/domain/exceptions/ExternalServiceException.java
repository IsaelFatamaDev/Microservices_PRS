package pe.edu.vallegrande.vgmsauthentication.domain.exceptions;

public class ExternalServiceException extends DomainException{

    public ExternalServiceException(String serviceName){
        super(
            String.format("Service '%s' is temporarily unavailable", serviceName), "EXTERNAL_SERVICE_UNAVAILABLE", 503
        );
    }

    public ExternalServiceException(String message, Throwable cause){
        super(
            String.format("Error communicating with service '%s'", message), cause, "EXTERNAL_SERVICE_ERROR", 503
        );
    }
}
