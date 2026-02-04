package pe.edu.vallegrande.vgmsauthentication.application.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IntrospectResponse {

    private Boolean active;

    @JsonProperty("token_type")
    private String tokenType;

    private String scope;

    @JsonProperty("client_id")
    private String clientId;

    private String username;

    private String sub;

    private Long exp;

    private Long iat;

    private String iss;

    private String aud;
}
