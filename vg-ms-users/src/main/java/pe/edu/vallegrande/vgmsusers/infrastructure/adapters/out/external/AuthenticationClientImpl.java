package pe.edu.vallegrande.vgmsusers.infrastructure.adapters.out.external;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import pe.edu.vallegrande.vgmsusers.domain.exceptions.ExternalServiceException;
import pe.edu.vallegrande.vgmsusers.domain.ports.out.IAuthenticationClient;
import reactor.core.publisher.Mono;

@Component
@Slf4j
@RequiredArgsConstructor
@SuppressWarnings("unused")
public class AuthenticationClientImpl implements IAuthenticationClient {

    private final WebClient.Builder webClientBuilder;

    @Value("${webclient.services.authentication.base-url}")
    private String authServiceUrl;

    private static final String SERVICE_NAME = "authenticationService";

    @Override
    @CircuitBreaker(name = SERVICE_NAME, fallbackMethod = "createUserFallback")
    @Retry(name = SERVICE_NAME)
    @TimeLimiter(name = SERVICE_NAME)
    public Mono<String> createUser(String userId, String email, String firstName, String lastName, String role) {
        log.info("Creating user with id: {}", userId);
        return webClientBuilder.build()
                .post()
                .uri(authServiceUrl + "/api/v1/auth/register")
                .bodyValue(new RegisterRequest(userId, email, firstName, lastName, role))
                .retrieve()
                .bodyToMono(RegisterResponse.class)
                .map(RegisterResponse::keycloakId)
                .doOnSuccess(id -> log.info("User created in keycloak: {}", id))
                .doOnError(e -> log.error("Error creating user in keycloak: {}", e.getMessage()));
    }

    @Override
    @CircuitBreaker(name = SERVICE_NAME, fallbackMethod = "disableUserFallback")
    @Retry(name = SERVICE_NAME)
    public Mono<Void> disableUser(String userId) {
        log.info("Disabling user with id: {}", userId);
        return webClientBuilder.build()
                .patch()
                .uri(authServiceUrl + "/api/v1/auth/users/{userId}/disable", userId)
                .retrieve()
                .bodyToMono(Void.class)
                .doOnSuccess(v -> log.info("User disabled in keycloak: {}", userId))
                .doOnError(e -> log.error("Error disabling user in keycloak: {}", e.getMessage()));
    }

    @Override
    @CircuitBreaker(name = SERVICE_NAME, fallbackMethod = "enableUserFallback")
    @Retry(name = SERVICE_NAME)
    public Mono<Void> enableUser(String userId) {
        log.info("Enabling user with id: {}", userId);
        return webClientBuilder.build()
                .patch()
                .uri(authServiceUrl + "/api/v1/auth/users/{userId}/enable", userId)
                .retrieve()
                .bodyToMono(Void.class)
                .doOnSuccess(v -> log.info("User enabled in keycloak: {}", userId))
                .doOnError(e -> log.error("Error enabling user in keycloak: {}", e.getMessage()));
    }

    @Override
    @CircuitBreaker(name = SERVICE_NAME, fallbackMethod = "deleteUserFallback")
    @Retry(name = SERVICE_NAME)
    public Mono<Void> deleteUser(String userId) {
        log.info("Deleting user with id: {}", userId);
        return webClientBuilder.build()
                .delete()
                .uri(authServiceUrl + "/api/v1/auth/users/{userId}", userId)
                .retrieve()
                .bodyToMono(Void.class)
                .doOnSuccess(v -> log.info("User deleted in keycloak: {}", userId))
                .doOnError(e -> log.error("Error deleting user in keycloak: {}", e.getMessage()));
    }

    private Mono<String> createUserFallback(
            String userId, String email, String firstName, String lastName, String role, Throwable t) {
        log.error("Circuit breaker: createUser fallback for {}: {}", userId, t.getMessage());
        return Mono.error(new ExternalServiceException("Authentication", t));
    }

    private Mono<Void> disableUserFallback(String userId, Throwable t) {
        log.error("Circuit breaker: disableUser fallback for {}: {}", userId, t.getMessage());
        return Mono.error(new ExternalServiceException("Authentication", t));
    }

    private Mono<Void> enableUserFallback(String userId, Throwable t) {
        log.error("Circuit breaker: enableUser fallback for {}: {}", userId, t.getMessage());
        return Mono.error(new ExternalServiceException("Authentication", t));
    }

    private Mono<Void> deleteUserFallback(String userId, Throwable t) {
        log.error("Circuit breaker: deleteUser fallback for {}: {}", userId, t.getMessage());
        return Mono.error(new ExternalServiceException("Authentication", t));
    }

    private record RegisterRequest(
            String userId, String email, String firstName, String lastName, String role) {
    }

    private record RegisterResponse(String keycloakId) {
    }
}
