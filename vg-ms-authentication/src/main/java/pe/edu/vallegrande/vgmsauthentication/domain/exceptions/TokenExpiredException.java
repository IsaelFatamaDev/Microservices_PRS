package pe.edu.vallegrande.vgmsauthentication.domain.exceptions;

public class TokenExpiredException extends DomainException{

    public TokenExpiredException(){
        super("Token has expired", "TOKEN_EXPIRED", 401);
    }

    public TokenExpiredException(String tokenType){
        super(String.format("%s token has expired", tokenType), "TOKEN_EXPIRED", 401);
    }

    public static TokenExpiredException accessToken(){
        return new TokenExpiredException("Access");
    }

    public static TokenExpiredException refreshToken(){
        return new TokenExpiredException("Refresh");
    }
}
