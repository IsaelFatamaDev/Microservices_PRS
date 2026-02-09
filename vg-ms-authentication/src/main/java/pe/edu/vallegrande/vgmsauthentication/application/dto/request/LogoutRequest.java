package pe.edu.vallegrande.vgmsauthentication.application.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LogoutRequest {

    private String refreshToken;

    @Builder.Default
    private String clientId = "jass-users-service";
}
