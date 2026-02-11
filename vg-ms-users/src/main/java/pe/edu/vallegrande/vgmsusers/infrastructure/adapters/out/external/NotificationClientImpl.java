package pe.edu.vallegrande.vgmsusers.infrastructure.adapters.out.external;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import pe.edu.vallegrande.vgmsusers.domain.ports.out.INotificationClient;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
@Slf4j
@SuppressWarnings("unused")
public class NotificationClientImpl implements INotificationClient {

    private final WebClient.Builder webClientBuilder;

    @Value("${webclient.services.notification.base-url}")
    private String notificationServiceUrl;

    private static final String SERVICE_NAME = "notificationService";

    @Override
    @CircuitBreaker(name = SERVICE_NAME, fallbackMethod = "sendWelcomeMessageFallback")
    @Retry(name = SERVICE_NAME)
    public Mono<Void> sendWelcomeMessage(String phone, String firstName, String organizationName) {
        log.info("Enviando mensaje de bienvenida a usuario: {}", phone);
        return webClientBuilder.build()
                .post()
                .uri(notificationServiceUrl + "/api/v1/notifications/whatsapp")
                .bodyValue(new WhatsAppMessage(
                        phone, "welcome", new WelcomeParams(firstName, organizationName)))
                .retrieve()
                .bodyToMono(void.class)
                .doOnSuccess(v -> log.info("Mensaje de bienvenida enviado a: {}", phone))
                .doOnError(e -> log.warn("Error al enviar el mensaje a {}: error - {}", phone, e.getMessage()));
    }

    @Override
    @CircuitBreaker(name = SERVICE_NAME, fallbackMethod = "sendProfileUpdatedNotificationFallback")
    @Retry(name = SERVICE_NAME)
    public Mono<Void> sendProfileUpdatedNotification(String phone, String firstName) {
        log.info("Enviando actualizacion del perfil a usuario: {}", phone);
        return webClientBuilder.build()
                .post()
                .uri(notificationServiceUrl + "/api/v1/notifications/whatsapp")
                .bodyValue(new WhatsAppMessage(
                        phone,
                        "profile-updated",
                        new ProfileUpdatedParams(firstName)))
                .retrieve()
                .bodyToMono(void.class)
                .doOnSuccess(v -> log.info("Notificacion de actualizacion de perfil enviada a: {}", phone))
                .doOnError(e -> log.warn("Error al enviar la notificacion a {}: error - {}", phone, e.getMessage()));
    }

    private Mono<Void> sendWelcomeMessageFallback(String phone, String firstName, String org, Throwable t) {
        log.warn("Failed to send welcome message to {}, will retry later: {}", phone, t.getMessage());
        return Mono.empty();
    }

    private Mono<Void> sendProfileUpdatedNotificationFallback(String phone, String firstName, Throwable t) {
        log.warn("Failed to send profile updated notification to {}, will retry later: {}", phone, t.getMessage());
        return Mono.empty();
    }

    public record WhatsAppMessage(String phone, String template, Object params) {
    }

    private record WelcomeParams(String firstName, String organizationName) {
    }

    private record ProfileUpdatedParams(String firstName) {
    }
}
