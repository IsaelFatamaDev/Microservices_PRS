package pe.edu.vallegrande.vgmsauthentication.domain.exceptions;

public class TokenInvalidException extends DomainException {

    public TokenInvalidException(){
        super("Token is invalid", "TOKEN_INVALID", 401);
    }

    public TokenInvalidException(String reason){
        super("Token is invalid: " + reason, "TOKEN_INVALID", 401);
    }

    public static TokenInvalidException malformed(){
        return new TokenInvalidException("Malformed token");
    }

    public static TokenInvalidException invalidSignature(){
        return new TokenInvalidException("Invalid signature");
    }

    public static TokenInvalidException revoked(){
        return new TokenInvalidException("Token has been revoked");
    }

    public static TokenInvalidException wrongIssuer(){
        return new TokenInvalidException("token issued by unknown authority");
    }
}
