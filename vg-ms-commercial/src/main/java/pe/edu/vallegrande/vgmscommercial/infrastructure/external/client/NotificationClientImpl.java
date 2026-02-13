package pe.edu.vallegrande.vgmscommercial.infrastructure.external.client;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import pe.edu.vallegrande.vgmscommercial.domain.ports.out.INotificationClient;
import reactor.core.publisher.Mono;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationClientImpl implements INotificationClient {

     @Qualifier("notificationWebClient")
     private final WebClient notificationWebClient;

     @Override
     @CircuitBreaker(name = "notificationService", fallbackMethod = "sendPaymentConfirmationFallback")
     @Retry(name = "notificationService")
     public Mono<Void> sendPaymentConfirmation(String userId, Double amount, String receiptNumber) {
          log.debug("Sending payment confirmation to user: {}", userId);
          return notificationWebClient.post()
                    .uri("/api/v1/notifications/payment-confirmation")
                    .bodyValue(Map.of("userId", userId, "amount", amount, "receiptNumber", receiptNumber))
                    .retrieve()
                    .bodyToMono(Void.class);
     }

     @Override
     @CircuitBreaker(name = "notificationService", fallbackMethod = "sendServiceCutWarningFallback")
     @Retry(name = "notificationService")
     public Mono<Void> sendServiceCutWarning(String userId, Double debtAmount, Integer daysToCut) {
          log.debug("Sending service cut warning to user: {}", userId);
          return notificationWebClient.post()
                    .uri("/api/v1/notifications/service-cut-warning")
                    .bodyValue(Map.of("userId", userId, "debtAmount", debtAmount, "daysToCut", daysToCut))
                    .retrieve()
                    .bodyToMono(Void.class);
     }

     @Override
     @CircuitBreaker(name = "notificationService", fallbackMethod = "sendServiceCutExecutedFallback")
     @Retry(name = "notificationService")
     public Mono<Void> sendServiceCutExecuted(String userId) {
          log.debug("Sending service cut executed notification to user: {}", userId);
          return notificationWebClient.post()
                    .uri("/api/v1/notifications/service-cut-executed")
                    .bodyValue(Map.of("userId", userId))
                    .retrieve()
                    .bodyToMono(Void.class);
     }

     @Override
     @CircuitBreaker(name = "notificationService", fallbackMethod = "sendServiceReconnectedFallback")
     @Retry(name = "notificationService")
     public Mono<Void> sendServiceReconnected(String userId) {
          log.debug("Sending service reconnected notification to user: {}", userId);
          return notificationWebClient.post()
                    .uri("/api/v1/notifications/service-reconnected")
                    .bodyValue(Map.of("userId", userId))
                    .retrieve()
                    .bodyToMono(Void.class);
     }

     private Mono<Void> sendPaymentConfirmationFallback(String userId, Double amount, String receiptNumber,
               Throwable t) {
          log.warn("Fallback for sendPaymentConfirmation: {} - {}", userId, t.getMessage());
          return Mono.empty();
     }

     private Mono<Void> sendServiceCutWarningFallback(String userId, Double debtAmount, Integer daysToCut,
               Throwable t) {
          log.warn("Fallback for sendServiceCutWarning: {} - {}", userId, t.getMessage());
          return Mono.empty();
     }

     private Mono<Void> sendServiceCutExecutedFallback(String userId, Throwable t) {
          log.warn("Fallback for sendServiceCutExecuted: {} - {}", userId, t.getMessage());
          return Mono.empty();
     }

     private Mono<Void> sendServiceReconnectedFallback(String userId, Throwable t) {
          log.warn("Fallback for sendServiceReconnected: {} - {}", userId, t.getMessage());
          return Mono.empty();
     }
}
