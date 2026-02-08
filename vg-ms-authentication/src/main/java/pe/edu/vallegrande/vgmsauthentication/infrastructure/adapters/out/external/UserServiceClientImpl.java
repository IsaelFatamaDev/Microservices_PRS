package pe.edu.vallegrande.vgmsauthentication.infrastructure.adapters.out.external;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import pe.edu.vallegrande.vgmsauthentication.domain.ports.out.IUserServiceClient;
import reactor.core.publisher.Mono;

import java.util.Map;

@Slf4j
@Component
public class UserServiceClientImpl implements IUserServiceClient {

    private final WebClient webClient;

    @SuppressWarnings("null")
    public UserServiceClientImpl(
            WebClient.Builder webClientBuilder,
            @Value("${services.users.url}") String usersServiceUrl) {
        this.webClient = webClientBuilder
                .baseUrl(usersServiceUrl)
                .build();
    }

    @Override
    @CircuitBreaker(name = "users-service")
    public Mono<Boolean> existsUser(String userId) {
        return webClient.head()
                .uri("/api/v1/users/{id}", userId)
                .retrieve()
                .toBodilessEntity()
                .map(response -> response.getStatusCode().is2xxSuccessful())
                .onErrorReturn(false);
    }

    @Override
    @CircuitBreaker(name = "users-service", fallbackMethod = "getUserByIdFallback")
    @Retry(name = "users-service")
    public Mono<UserInfo> getUserById(String userId) {
        log.debug("Getting user by ID: {}", userId);

        return webClient.get()
                .uri("/api/v1/users/{id}", userId)
                .retrieve()
                .bodyToMono(Map.class)
                .map(this::mapToUserInfo);
    }

    @Override
    @CircuitBreaker(name = "users-service", fallbackMethod = "getUserByEmailFallback")
    @Retry(name = "users-service")
    public Mono<UserInfo> getUserByEmail(String email) {
        log.debug("Getting user by email: {}", email);

        return webClient.get()
                .uri("/api/v1/users/email/{email}", email)
                .retrieve()
                .bodyToMono(Map.class)
                .map(this::mapToUserInfo);
    }

    @SuppressWarnings("unused")
    private Mono<UserInfo> getUserByIdFallback(String userId, Throwable t) {
        log.warn("Users service unavailable, using fallback for userId: {}", userId);
        return Mono.empty();
    }

    @SuppressWarnings("unused")
    private Mono<UserInfo> getUserByEmailFallback(String email, Throwable t) {
        log.warn("Users service unavailable, using fallback for email: {}", email);
        return Mono.empty();
    }

    @SuppressWarnings("unchecked")
    private UserInfo mapToUserInfo(Map<?, ?> response) {
        Map<String, Object> data = (Map<String, Object>) response.get("data");
        if (data == null) {
            data = (Map<String, Object>) response;
        }

        return new UserInfo(
                getString(data, "id"),
                getString(data, "organizationId"),
                getString(data, "email"),
                getString(data, "firstName"),
                getString(data, "lastName"),
                getString(data, "role"));
    }

    private String getString(Map<String, Object> map, String key) {
        Object value = map.get(key);
        return value != null ? value.toString() : null;
    }
}
