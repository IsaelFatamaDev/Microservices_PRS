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
    return userServiceClient.getUserByEmail(username)
        .map(userInfo -> {
            Map<String, Object> enriched = new HashMap<>(tokens);
            enriched.put("user_id", userInfo.id());
            enriched.put("organization_id", userInfo.organizationId());
            enriched.put("role", userInfo.role());
            enriched.put("full_name", userInfo.firstName() + " " + userInfo.lastName());
            return enriched;
        })
        .onErrorResume(error ->{
            log.warn("Could not enrich user info: {}", error.getMessage());
            return Mono.just(tokens);
        });
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
