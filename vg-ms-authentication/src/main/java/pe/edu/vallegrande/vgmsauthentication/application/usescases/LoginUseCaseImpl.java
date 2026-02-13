package pe.edu.vallegrande.vgmsauthentication.application.usescases;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pe.edu.vallegrande.vgmsauthentication.domain.exceptions.InvalidCredentialsException;
import pe.edu.vallegrande.vgmsauthentication.domain.exceptions.KeycloakException;
import pe.edu.vallegrande.vgmsauthentication.domain.models.UserCredentials;
import pe.edu.vallegrande.vgmsauthentication.domain.ports.in.ILoginUseCase;
import pe.edu.vallegrande.vgmsauthentication.domain.ports.out.IKeycloakClient;
import pe.edu.vallegrande.vgmsauthentication.domain.ports.out.IUserServiceClient;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class LoginUseCaseImpl implements ILoginUseCase {

    private final IKeycloakClient keycloakClient;
    private final IUserServiceClient userServiceClient;

    @Override
    public Mono<Map<String, Object>> execute(UserCredentials credentials) {
        log.info("Attempting login for user: {}", credentials.getUsername());

        return keycloakClient.getTokenWithPassword(credentials.getUsername(), credentials.getPassword(), credentials.getClientId())
            .flatMap(tokens -> enrichWithUserInfo(tokens, credentials.getUsername()))
            .doOnSuccess(result -> log.info("Login successful for user: {}", credentials.getUsername()))
            .doOnError(error -> log.error("Login failed for user: {}, Reason: {}", credentials.getUsername(), error.getMessage()))
            .onErrorMap(this::mapKeycloakError);
    }

    private Mono<Map<String, Object>> enrichWithUserInfo(Map<String, Object> tokens, String username) {
        String accessToken = (String) tokens.get("access_token");
        String userId = extractUserIdFromToken(accessToken);

        return userServiceClient.getUserById(userId)
            .map(userInfo -> {
                Map<String, Object> enriched = new HashMap<>(tokens);
                enriched.put("user_id", userInfo.id());
                enriched.put("organization_id", userInfo.organizationId());
                enriched.put("role", userInfo.role());
                enriched.put("full_name", userInfo.firstName() + " " + userInfo.lastName());
                return enriched;
            })
            .onErrorResume(error -> {
                log.warn("Could not enrich user info for user {}: {}", username, error.getMessage());
                return Mono.just(tokens);
            });
    }

    private String extractUserIdFromToken(String token) {
        try {
            String[] parts = token.split("\\.");
            if (parts.length < 2) {
                return null;
            }
            String payload = new String(java.util.Base64.getUrlDecoder().decode(parts[1]));
            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            com.fasterxml.jackson.databind.JsonNode node = mapper.readTree(payload);
            
            if (node.has("userId")) {
                return node.get("userId").asText();
            }
            if (node.has("sub")) {
                return node.get("sub").asText();
            }
        } catch (Exception e) {
            log.warn("Error parsing token to extract userId: {}", e.getMessage());
        }
        return null;
    }

    private Throwable mapKeycloakError(Throwable error) {
        String message = error.getMessage();

        if (message != null && message.contains("invalid_grant")){
            return new InvalidCredentialsException();
        }

        if(message != null && message.contains("disabled")){
            return InvalidCredentialsException.userDisabled();
        }

        if(message != null && message.contains("locked")){
            return InvalidCredentialsException.accountLocked();
        }

        return new KeycloakException("Authentication failed", error);
    }

}
