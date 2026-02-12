package pe.edu.vallegrande.msdistribution.domain.exceptions.base;

public abstract class DomainException extends RuntimeException {
    private final String errorCode;
    private final Integer httpStatus;

    protected DomainException(String message, String errorCode, Integer httpStatus) {
        super(message);
        this.errorCode = errorCode;
        this.httpStatus = httpStatus;
    }

    protected DomainException(String message, String errorCode, Integer httpStatus, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
        this.httpStatus = httpStatus;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public Integer getHttpStatus() {
        return httpStatus;
    }
}
