package pe.edu.vallegrande.vgmscommercial.infrastructure.external.client;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import pe.edu.vallegrande.vgmscommercial.domain.ports.out.IInfrastructureClient;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class InfrastructureClientImpl implements IInfrastructureClient {

     @Qualifier("infrastructureWebClient")
     private final WebClient infrastructureWebClient;

     @Override
     @CircuitBreaker(name = "infrastructureService", fallbackMethod = "existsWaterBoxFallback")
     @Retry(name = "infrastructureService")
     @TimeLimiter(name = "infrastructureService")
     public Mono<Boolean> existsWaterBox(String waterBoxId) {
          log.debug("Checking if water box exists: {}", waterBoxId);
          return infrastructureWebClient.get()
                    .uri("/api/v1/water-boxes/{id}", waterBoxId)
                    .retrieve()
                    .bodyToMono(Object.class)
                    .map(response -> true)
                    .onErrorReturn(false);
     }

     @Override
     @CircuitBreaker(name = "infrastructureService", fallbackMethod = "getWaterBoxByUserIdFallback")
     @Retry(name = "infrastructureService")
     public Mono<String> getWaterBoxByUserId(String userId) {
          log.debug("Getting water box for user: {}", userId);
          return infrastructureWebClient.get()
                    .uri("/api/v1/water-boxes/user/{userId}", userId)
                    .retrieve()
                    .bodyToMono(String.class)
                    .onErrorReturn("N/A");
     }

     @Override
     @CircuitBreaker(name = "infrastructureService", fallbackMethod = "updateWaterBoxStatusFallback")
     @Retry(name = "infrastructureService")
     public Mono<Void> updateWaterBoxStatus(String waterBoxId, String status) {
          log.debug("Updating water box status: {} to {}", waterBoxId, status);
          return infrastructureWebClient.patch()
                    .uri("/api/v1/water-boxes/{id}/status?status={status}", waterBoxId, status)
                    .retrieve()
                    .bodyToMono(Void.class);
     }

     private Mono<Boolean> existsWaterBoxFallback(String waterBoxId, Throwable t) {
          log.warn("Fallback for existsWaterBox: {} - {}", waterBoxId, t.getMessage());
          return Mono.just(true);
     }

     private Mono<String> getWaterBoxByUserIdFallback(String userId, Throwable t) {
          log.warn("Fallback for getWaterBoxByUserId: {} - {}", userId, t.getMessage());
          return Mono.just("N/A");
     }

     private Mono<Void> updateWaterBoxStatusFallback(String waterBoxId, String status, Throwable t) {
          log.warn("Fallback for updateWaterBoxStatus: {} to {} - {}", waterBoxId, status, t.getMessage());
          return Mono.empty();
     }
}
