package pe.edu.vallegrande.vgmsauthentication.infrastructure.adapters.out.external;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import pe.edu.vallegrande.vgmsauthentication.domain.exceptions.InvalidCredentialsException;
import pe.edu.vallegrande.vgmsauthentication.domain.exceptions.KeycloakException;
import pe.edu.vallegrande.vgmsauthentication.domain.ports.out.IKeycloakClient;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
@SuppressWarnings("null")
public class KeycloakClientImpl implements IKeycloakClient {

    private final WebClient tokenWebClient;
    private final WebClient adminWebClient;

    @Value("${keycloak.realm}")
    private String realm;

    @Value("${keycloak.auth-server-url}")
    private String authServerUrl;

    @Value("${keycloak.client-secret}")
    private String clientSecret;

    @Value("${keycloak.admin.client-id:admin-cli}")
    private String adminClientId;

    @Value("${keycloak.admin.client-secret:}")
    private String adminClientSecret;

    public KeycloakClientImpl(
            WebClient.Builder webClientBuilder,
            @Value("${keycloak.auth-server-url}") String authServerUrl,
            @Value("${keycloak.realm}") String realm) {
        String tokenUrl = authServerUrl + "/realms/" + realm;
        String adminUrl = authServerUrl + "/admin/realms/" + realm;

        this.tokenWebClient = webClientBuilder.baseUrl(tokenUrl).build();
        this.adminWebClient = webClientBuilder.baseUrl(adminUrl).build();
    }

    @Override
    @CircuitBreaker(name = "keycloak", fallbackMethod = "loginFallback")
    @Retry(name = "keycloak")
    public Mono<Map<String, Object>> getTokenWithPassword(String username, String password, String clientId) {
        log.debug("Requesting token for user: {}", username);

        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("grant_type", "password");
        formData.add("client_id", clientId);
        formData.add("client_secret", clientSecret);
        formData.add("username", username);
        formData.add("password", password);

        return tokenWebClient.post()
                .uri("/protocol/openid-connect/token")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(BodyInserters.fromFormData(formData))
                .retrieve()
                .onStatus(
                        status -> status.value() == 401,
                        response -> Mono.error(new InvalidCredentialsException()))
                .onStatus(
                        HttpStatusCode::is5xxServerError,
                        response -> Mono.error(KeycloakException.connectionError()))
                .bodyToMono(Map.class)
                .map(this::convertToStringObjectMap)
                .doOnSuccess(tokens -> log.debug("Token obtained successfully"))
                .doOnError(error -> log.warn("Token request failed: {}", error.getMessage()));
    }

    @SuppressWarnings("unused") // Used by Resilience4j circuit breaker via reflection
    private Mono<Map<String, Object>> loginFallback(
            String username, String password, String clientId, Throwable t) {
        log.error("Keycloak circuit breaker opened for login. Error: {}", t.getMessage());
        return Mono.error(KeycloakException.connectionError());
    }

    @Override
    @CircuitBreaker(name = "keycloak")
    @Retry(name = "keycloak")
    public Mono<Map<String, Object>> refreshToken(String refreshToken, String clientId) {
        log.debug("Refreshing token");

        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("grant_type", "refresh_token");
        formData.add("client_id", clientId);
        formData.add("refresh_token", refreshToken);

        return tokenWebClient.post()
                .uri("/protocol/openid-connect/token")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(BodyInserters.fromFormData(formData))
                .retrieve()
                .bodyToMono(Map.class)
                .map(this::convertToStringObjectMap);
    }

    @Override
    public Mono<Void> revokeToken(String refreshToken, String clientId) {
        log.debug("Revoking token");

        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("client_id", clientId);
        formData.add("refresh_token", refreshToken);

        return adminWebClient.post()
                .uri("/protocol/openid-connect/logout")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(BodyInserters.fromFormData(formData))
                .retrieve()
                .bodyToMono(Void.class);
    }

    @Override
    @CircuitBreaker(name = "keycloak")
    public Mono<Map<String, Object>> introspectToken(String token, String clientId, String clientSecret) {
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("token", token);
        formData.add("client_id", clientId);
        if (clientSecret != null) {
            formData.add("client_secret", clientSecret);
        }

        return tokenWebClient.post()
                .uri("/protocol/openid-connect/token/introspect")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(BodyInserters.fromFormData(formData))
                .retrieve()
                .bodyToMono(Map.class)
                .map(this::convertToStringObjectMap);
    }

    @Override
    @CircuitBreaker(name = "keycloak")
    public Mono<Map<String, Object>> getUserInfo(String accessToken) {
        return tokenWebClient.get()
                .uri("/protocol/openid-connect/userinfo")
                .header("Authorization", "Bearer " + accessToken)
                .retrieve()
                .bodyToMono(Map.class)
                .map(this::convertToStringObjectMap);
    }

    @Override
    @CircuitBreaker(name = "keycloak-admin")
    public Mono<String> createUser(
            String userId,
            String email,
            String username,
            String firstName,
            String lastName,
            String password,
            String role,
            String organizationId) {
        log.info("Creating user in Keycloak: {}", email);

        Map<String, Object> userRepresentation = new HashMap<>();
        // NO establecer "id" - Keycloak lo genera automáticamente
        userRepresentation.put("email", email);
        userRepresentation.put("username", username);
        userRepresentation.put("firstName", firstName);
        userRepresentation.put("lastName", lastName);
        userRepresentation.put("enabled", true);
        userRepresentation.put("emailVerified", true);

        Map<String, Object> credentials = new HashMap<>();
        credentials.put("type", "password");
        credentials.put("temporary", false);
        credentials.put("value", password);

        userRepresentation.put("credentials", new Object[] { credentials });

        return getAdminToken()
                .flatMap(adminToken -> adminWebClient.post()
                        .uri("/users")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(userRepresentation)
                        .retrieve()
                        .toBodilessEntity()
                        .map(response -> {
                            String location = response.getHeaders().getFirst("Location");
                            return extractUserId(location);
                        }))
                .flatMap(keycloakUserId -> {
                    log.info("User created in Keycloak with ID: {}, now updating attributes", keycloakUserId);
                    // Actualizar atributos en llamada separada después de la creación
                    return updateUserAttributes(keycloakUserId, userId, organizationId)
                            .then(assignRole(keycloakUserId, role))
                            .thenReturn(keycloakUserId);
                });
    }

    /**
     * Actualiza los atributos personalizados del usuario en Keycloak
     */
    private Mono<Void> updateUserAttributes(String keycloakUserId, String userId, String organizationId) {
        log.info("Updating custom attributes for Keycloak user: {}", keycloakUserId);

        Map<String, Object> updates = new HashMap<>();
        Map<String, java.util.List<String>> attributes = new HashMap<>();
        attributes.put("userId", java.util.List.of(userId));
        attributes.put("organizationId", java.util.List.of(organizationId));
        updates.put("attributes", attributes);

        return getAdminToken()
                .flatMap(adminToken -> adminWebClient.put()
                        .uri("/users/{userId}", keycloakUserId)
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(updates)
                        .retrieve()
                        .bodyToMono(Void.class))
                .doOnSuccess(v -> log.info("Attributes updated successfully for user: {}", keycloakUserId))
                .doOnError(error -> log.error("Failed to update attributes for user: {}", keycloakUserId, error));
    }

    @Override
    @CircuitBreaker(name = "keycloak-admin")
    public Mono<Void> updateUser(
            String userId,
            String email,
            String firstName,
            String lastName) {
        log.info("Updating user in Keycloak: {}", userId);

        Map<String, Object> updates = new HashMap<>();
        if (email != null) {
            updates.put("email", email);
            updates.put("username", email);
        }
        if (firstName != null)
            updates.put("firstName", firstName);
        if (lastName != null)
            updates.put("lastName", lastName);

        return getAdminToken()
                .flatMap(adminToken -> adminWebClient.put()
                        .uri("/users/{userId}", userId)
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(updates)
                        .retrieve()
                        .bodyToMono(Void.class));
    }

    @Override
    @CircuitBreaker(name = "keycloak-admin")
    public Mono<Void> disableUser(String userId) {
        log.info("Disabling user in Keycloak: {}", userId);

        Map<String, Object> updates = new HashMap<>();
        updates.put("enabled", false);

        return getAdminToken()
                .flatMap(adminToken -> adminWebClient.put()
                        .uri("/users/{userId}", userId)
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(updates)
                        .retrieve()
                        .bodyToMono(Void.class));
    }

    @Override
    @CircuitBreaker(name = "keycloak-admin")
    public Mono<Void> enableUser(String userId) {
        log.info("Enabling user in Keycloak: {}", userId);

        Map<String, Object> updates = new HashMap<>();
        updates.put("enabled", true);

        return getAdminToken()
                .flatMap(adminToken -> adminWebClient.put()
                        .uri("/users/{userId}", userId)
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(updates)
                        .retrieve()
                        .bodyToMono(Void.class));
    }

    @Override
    @CircuitBreaker(name = "keycloak-admin")
    public Mono<Void> deleteUser(String userId) {
        log.info("Deleting user from Keycloak: {}", userId);

        return getAdminToken()
                .flatMap(adminToken -> adminWebClient.delete()
                        .uri("/users/{userId}", userId)
                        .header("Authorization", "Bearer " + adminToken)
                        .retrieve()
                        .bodyToMono(Void.class));
    }

    @Override
    @CircuitBreaker(name = "keycloak-admin")
    public Mono<Void> assignRole(String userId, String roleName) {
        log.info("Assigning role '{}' to user: {}", roleName, userId);

        return getAdminToken()
                .flatMap(adminToken -> adminWebClient.get()
                        .uri("/roles/{roleName}", roleName)
                        .header("Authorization", "Bearer " + adminToken)
                        .retrieve()
                        .bodyToMono(Map.class)
                        .flatMap(role -> adminWebClient.post()
                                .uri("/users/{userId}/role-mappings/realm", userId)
                                .header("Authorization", "Bearer " + adminToken)
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(new Object[] { role })
                                .retrieve()
                                .bodyToMono(Void.class)));
    }

    @Override
    @CircuitBreaker(name = "keycloak-admin")
    public Mono<Void> removeRole(String userId, String roleName) {
        log.info("Removing role '{}' from user: {}", roleName, userId);

        return getAdminToken()
                .flatMap(adminToken -> adminWebClient.get()
                        .uri("/roles/{roleName}", roleName)
                        .header("Authorization", "Bearer " + adminToken)
                        .retrieve()
                        .bodyToMono(Map.class)
                        .flatMap(role -> adminWebClient.method(org.springframework.http.HttpMethod.DELETE)
                                .uri("/users/{userId}/role-mappings/realm", userId)
                                .header("Authorization", "Bearer " + adminToken)
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(new Object[] { role })
                                .retrieve()
                                .bodyToMono(Void.class)));
    }

    @Override
    @CircuitBreaker(name = "keycloak-admin")
    public Mono<Void> resetPassword(String userId, String newPassword, boolean temporary) {
        log.info("Resetting password for user: {}", userId);

        Map<String, Object> credentials = new HashMap<>();
        credentials.put("type", "password");
        credentials.put("value", newPassword);
        credentials.put("temporary", temporary);

        return getAdminToken()
                .flatMap(adminToken -> adminWebClient.put()
                        .uri("/users/{userId}/reset-password", userId)
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(credentials)
                        .retrieve()
                        .bodyToMono(Void.class));
    }

    @CircuitBreaker(name = "keycloak-admin")
    public Mono<Void> configureClientMappers(String clientId) {
        log.info("Configuring user attribute mappers for client: {}", clientId);

        return getAdminToken()
                .flatMap(adminToken -> getClientUuid(clientId, adminToken)
                        .flatMap(clientUuid -> createUserAttributeMapper(clientUuid, "userId", "userId", adminToken)
                                .then(createUserAttributeMapper(clientUuid, "organizationId", "organizationId",
                                        adminToken))))
                .doOnSuccess(v -> log.info("Client mappers configured successfully"))
                .doOnError(error -> log.error("Failed to configure client mappers: {}", error.getMessage()));
    }

    private Mono<String> getClientUuid(String clientId, String adminToken) {
        return adminWebClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/clients")
                        .queryParam("clientId", clientId)
                        .build())
                .header("Authorization", "Bearer " + adminToken)
                .retrieve()
                .bodyToMono(java.util.List.class)
                .map(clients -> {
                    if (clients.isEmpty()) {
                        throw new RuntimeException("Client not found: " + clientId);
                    }
                    Map<?, ?> client = (Map<?, ?>) clients.get(0);
                    return (String) client.get("id");
                });
    }

    private Mono<Void> createUserAttributeMapper(String clientUuid, String mapperName, String attributeName,
            String adminToken) {
        Map<String, Object> mapper = new HashMap<>();
        mapper.put("name", mapperName);
        mapper.put("protocol", "openid-connect");
        mapper.put("protocolMapper", "oidc-usermodel-attribute-mapper");

        Map<String, Object> config = new HashMap<>();
        config.put("user.attribute", attributeName);
        config.put("claim.name", attributeName);
        config.put("jsonType.label", "String");
        config.put("id.token.claim", "true");
        config.put("access.token.claim", "true");
        config.put("userinfo.token.claim", "true");
        mapper.put("config", config);

        return adminWebClient.post()
                .uri("/clients/{clientUuid}/protocol-mappers/models", clientUuid)
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(mapper)
                .retrieve()
                .onStatus(status -> status.value() == 409, response -> {
                    log.info("Mapper {} already exists, skipping", mapperName);
                    return Mono.empty();
                })
                .bodyToMono(Void.class)
                .onErrorResume(error -> {
                    if (error.getMessage() != null && error.getMessage().contains("409")) {
                        return Mono.empty();
                    }
                    return Mono.error(error);
                });
    }

    private Mono<String> getAdminToken() {
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("grant_type", "client_credentials");
        formData.add("client_id", adminClientId);
        formData.add("client_secret", adminClientSecret);

        return tokenWebClient.post()
                .uri("/protocol/openid-connect/token")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(BodyInserters.fromFormData(formData))
                .retrieve()
                .bodyToMono(Map.class)
                .map(response -> (String) response.get("access_token"));
    }

    private String extractUserId(String location) {
        if (location == null)
            return null;
        String[] parts = location.split("/");
        return parts[parts.length - 1];
    }

    private Map<String, Object> convertToStringObjectMap(Map<?, ?> map) {
        Map<String, Object> result = new HashMap<>();
        map.forEach((key, value) -> result.put(key.toString(), value));
        return result;
    }

}
