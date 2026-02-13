package pe.edu.vallegrande.vgmscommercial.infrastructure.external.client;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import pe.edu.vallegrande.vgmscommercial.domain.ports.out.IUserServiceClient;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserClientImpl implements IUserServiceClient {

     @Qualifier("userWebClient")
     private final WebClient userWebClient;

     @Override
     @CircuitBreaker(name = "userService", fallbackMethod = "existsByIdFallback")
     @Retry(name = "userService")
     @TimeLimiter(name = "userService")
     public Mono<Boolean> existsById(String userId, String organizationId) {
          log.debug("Checking if user exists: {}", userId);
          return userWebClient.get()
                    .uri("/api/v1/users/{id}?organizationId={orgId}", userId, organizationId)
                    .retrieve()
                    .bodyToMono(Object.class)
                    .map(response -> true)
                    .onErrorReturn(false);
     }

     @Override
     @CircuitBreaker(name = "userService", fallbackMethod = "getUserFullNameFallback")
     @Retry(name = "userService")
     public Mono<String> getUserFullName(String userId) {
          log.debug("Getting user full name: {}", userId);
          return userWebClient.get()
                    .uri("/api/v1/users/{id}/name", userId)
                    .retrieve()
                    .bodyToMono(String.class)
                    .onErrorReturn("Unknown User");
     }

     @Override
     @CircuitBreaker(name = "userService", fallbackMethod = "getUserPhoneFallback")
     @Retry(name = "userService")
     public Mono<String> getUserPhone(String userId) {
          log.debug("Getting user phone: {}", userId);
          return userWebClient.get()
                    .uri("/api/v1/users/{id}/phone", userId)
                    .retrieve()
                    .bodyToMono(String.class)
                    .onErrorReturn("N/A");
     }

     private Mono<Boolean> existsByIdFallback(String userId, String organizationId, Throwable t) {
          log.warn("Fallback for existsById user: {} - {}", userId, t.getMessage());
          return Mono.just(true);
     }

     private Mono<String> getUserFullNameFallback(String userId, Throwable t) {
          log.warn("Fallback for getUserFullName: {} - {}", userId, t.getMessage());
          return Mono.just("Unknown User");
     }

     private Mono<String> getUserPhoneFallback(String userId, Throwable t) {
          log.warn("Fallback for getUserPhone: {} - {}", userId, t.getMessage());
          return Mono.just("N/A");
     }
}
