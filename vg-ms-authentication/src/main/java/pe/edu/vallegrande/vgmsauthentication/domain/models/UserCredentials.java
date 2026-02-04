package pe.edu.vallegrande.vgmsauthentication.domain.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserCredentials {

    private String username;
    private String password;
    private String clientId;
    private String clientSecret;
    private String grantType;

    public static UserCredentials forPasswordGrant(
        String username, String password, String clientId
    ){
        return UserCredentials.builder()
            .username(username)
            .password(password)
            .clientId(clientId)
            .grantType("password")
            .build();
    }

    public static UserCredentials forRefreshGrant(
        String refreshToken, String clientId
    ){
        return UserCredentials.builder()
            .password(refreshToken)
            .clientId(clientId)
            .grantType("refresh_token")
            .build();
    }

    public boolean isPasswordGrant(){
        return "password".equals(grantType);
    }

    public boolean isRefreshGrant(){
        return "refresh_token".equals(grantType);
    }

    public boolean isConfidentialClient(){
        return clientSecret != null && !clientSecret.isBlank();
    }

}
