package pe.edu.vallegrande.vgmsauthentication.application.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserInfoResponse {

    private String sub;
    private String email;

    @JsonProperty("email_verified")
    private Boolean emailVerified;

    @JsonProperty("preferred_username")
    private String preferredUsername;

    @JsonProperty("given_name")
    private String givenName;

    @JsonProperty("family_name")
    private String familyName;

    private String name;

    @JsonProperty("realm_roles")
    private List<String> realmRoles;

    @JsonProperty("client_roles")
    private List<String> clientRoles;

    @JsonProperty("organization_id")
    private String organizationId;
}
