package pe.edu.vallegrande.vgmsauthentication.application.mappers;

import pe.edu.vallegrande.vgmsauthentication.application.dto.request.LoginRequest;
import pe.edu.vallegrande.vgmsauthentication.application.dto.response.IntrospectResponse;
import pe.edu.vallegrande.vgmsauthentication.application.dto.response.LoginResponse;
import pe.edu.vallegrande.vgmsauthentication.application.dto.response.TokenResponse;
import pe.edu.vallegrande.vgmsauthentication.application.dto.response.UserInfoResponse;
import pe.edu.vallegrande.vgmsauthentication.domain.models.UserCredentials;

import java.util.Map;

public final class AuthMapper {

    private AuthMapper() {
    }

    public static UserCredentials toCredentials(LoginRequest request) {
        return UserCredentials.forPasswordGrant(
                request.getUsername(),
                request.getPassword(),
                request.getClientId());
    }

    public static LoginResponse toLoginResponse(Map<String, Object> keycloakResponse) {
        return LoginResponse.builder()
                .accessToken(getString(keycloakResponse, "access_token"))
                .refreshToken(getString(keycloakResponse, "refresh_token"))
                .tokenType(getString(keycloakResponse, "token_type", "Bearer"))
                .expiresIn(getLong(keycloakResponse, "expires_in"))
                .refreshExpiresIn(getLong(keycloakResponse, "refresh_expires_in"))
                .userId(getString(keycloakResponse, "user_id"))
                .organizationId(getString(keycloakResponse, "organization_id"))
                .email(getString(keycloakResponse, "email"))
                .fullName(getString(keycloakResponse, "full_name"))
                .role(getString(keycloakResponse, "role"))
                .scope(getString(keycloakResponse, "scope"))
                .build();
    }

    public static TokenResponse toTokenResponse(Map<String, Object> keycloakResponse) {
        return TokenResponse.builder()
                .accessToken(getString(keycloakResponse, "access_token"))
                .refreshToken(getString(keycloakResponse, "refresh_token"))
                .tokenType(getString(keycloakResponse, "token_type", "Bearer"))
                .expiresIn(getLong(keycloakResponse, "expires_in"))
                .refreshExpiresIn(getLong(keycloakResponse, "refresh_expires_in"))
                .scope(getString(keycloakResponse, "scope"))
                .build();
    }

    @SuppressWarnings("unchecked")
    public static UserInfoResponse toUserInfoResponse(Map<String, Object> userInfo) {
        return UserInfoResponse.builder()
                .sub(getString(userInfo, "sub"))
                .email(getString(userInfo, "email"))
                .emailVerified(getBoolen(userInfo, "email_verified"))
                .preferredUsername(getString(userInfo, "preferred_username"))
                .givenName(getString(userInfo, "given_name"))
                .familyName(getString(userInfo, "family_name"))
                .name(getString(userInfo, "name"))
                .realmRoles((java.util.List<String>) userInfo.get("realm_roles"))
                .clientRoles((java.util.List<String>) userInfo.get("client_roles"))
                .organizationId(getString(userInfo, "organization_id"))
                .build();
    }

    public static IntrospectResponse toIntrospectResponse(Map<String, Object> introspection) {
        return IntrospectResponse.builder()
                .active(getBoolen(introspection, "active"))
                .tokenType(getString(introspection, "token_type"))
                .scope(getString(introspection, "scope"))
                .clientId(getString(introspection, "client_id"))
                .username(getString(introspection, "username"))
                .sub(getString(introspection, "sub"))
                .exp(getLong(introspection, "exp"))
                .iat(getLong(introspection, "iat"))
                .iss(getString(introspection, "iss"))
                .aud(getString(introspection, "aud"))
                .build();
    }

    private static String getString(Map<String, Object> map, String key) {
        Object value = map.get(key);
        return value != null ? value.toString() : null;
    }

    private static String getString(Map<String, Object> map, String key, String defaultValue) {
        Object value = map.get(key);
        return value != null ? value.toString() : defaultValue;
    }

    private static Long getLong(Map<String, Object> map, String key) {
        Object value = map.get(key);
        if (value == null)
            return null;
        if (value instanceof Number)
            return ((Number) value).longValue();
        try {
            return Long.parseLong(value.toString());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public static Boolean getBoolen(Map<String, Object> map, String key) {
        Object value = map.get(key);
        if (value == null)
            return null;
        if (value instanceof Boolean)
            return (Boolean) value;
        return Boolean.parseBoolean(value.toString());
    }
}
