package pe.edu.vallegrande.vgmscommercial.infrastructure.external.client;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import pe.edu.vallegrande.vgmscommercial.domain.ports.out.IOrganizationClient;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrganizationClientImpl implements IOrganizationClient {

     @Qualifier("organizationWebClient")
     private final WebClient organizationWebClient;

     @Override
     @CircuitBreaker(name = "organizationService", fallbackMethod = "existsByIdFallback")
     @Retry(name = "organizationService")
     @TimeLimiter(name = "organizationService")
     public Mono<Boolean> existsById(String organizationId) {
          log.debug("Checking if organization exists: {}", organizationId);
          return organizationWebClient.get()
                    .uri("/api/v1/organizations/{id}", organizationId)
                    .retrieve()
                    .bodyToMono(Object.class)
                    .map(response -> true)
                    .onErrorReturn(false);
     }

     @Override
     @CircuitBreaker(name = "organizationService", fallbackMethod = "getMonthlyFeeFallback")
     @Retry(name = "organizationService")
     public Mono<Double> getMonthlyFee(String organizationId) {
          log.debug("Getting monthly fee for organization: {}", organizationId);
          return organizationWebClient.get()
                    .uri("/api/v1/organizations/{id}/monthly-fee", organizationId)
                    .retrieve()
                    .bodyToMono(Double.class)
                    .onErrorReturn(10.0);
     }

     @Override
     @CircuitBreaker(name = "organizationService", fallbackMethod = "getGracePeriodDaysFallback")
     @Retry(name = "organizationService")
     public Mono<Integer> getGracePeriodDays(String organizationId) {
          log.debug("Getting grace period days for organization: {}", organizationId);
          return organizationWebClient.get()
                    .uri("/api/v1/organizations/{id}/grace-period", organizationId)
                    .retrieve()
                    .bodyToMono(Integer.class)
                    .onErrorReturn(30);
     }

     @Override
     @CircuitBreaker(name = "organizationService", fallbackMethod = "getLateFeeRateFallback")
     @Retry(name = "organizationService")
     public Mono<Double> getLateFeeRate(String organizationId) {
          log.debug("Getting late fee rate for organization: {}", organizationId);
          return organizationWebClient.get()
                    .uri("/api/v1/organizations/{id}/late-fee-rate", organizationId)
                    .retrieve()
                    .bodyToMono(Double.class)
                    .onErrorReturn(1.0);
     }

     private Mono<Boolean> existsByIdFallback(String organizationId, Throwable t) {
          log.warn("Fallback for existsById organization: {} - {}", organizationId, t.getMessage());
          return Mono.just(true);
     }

     private Mono<Double> getMonthlyFeeFallback(String organizationId, Throwable t) {
          log.warn("Fallback for getMonthlyFee: {} - {}", organizationId, t.getMessage());
          return Mono.just(10.0);
     }

     private Mono<Integer> getGracePeriodDaysFallback(String organizationId, Throwable t) {
          log.warn("Fallback for getGracePeriodDays: {} - {}", organizationId, t.getMessage());
          return Mono.just(30);
     }

     private Mono<Double> getLateFeeRateFallback(String organizationId, Throwable t) {
          log.warn("Fallback for getLateFeeRate: {} - {}", organizationId, t.getMessage());
          return Mono.just(1.0);
     }
}
