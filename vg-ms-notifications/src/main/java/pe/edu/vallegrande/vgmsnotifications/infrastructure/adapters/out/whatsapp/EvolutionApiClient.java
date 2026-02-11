package pe.edu.vallegrande.vgmsnotifications.infrastructure.adapters.out.whatsapp;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import pe.edu.vallegrande.vgmsnotifications.domain.exceptions.NotificationFailedException;
import pe.edu.vallegrande.vgmsnotifications.domain.ports.out.IWhatsAppClient;
import reactor.core.publisher.Mono;

import java.util.Map;

@Slf4j
@Component
@SuppressWarnings("null")
public class EvolutionApiClient implements IWhatsAppClient {

    private final WebClient webClient;
    private final String instanceName;

    public EvolutionApiClient(
            WebClient.Builder webClientBuilder,
            @Value("${app.evolution-api.base-url}") String baseUrl,
            @Value("${app.evolution-api.api-key}") String apiKey,
            @Value("${app.evolution-api.instance-name}") String instanceName) {

        this.instanceName = instanceName;
        this.webClient = webClientBuilder
                .baseUrl(baseUrl)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader("apikey", apiKey)
                .build();
    }

    @Override
    public Mono<Boolean> sendTextMessage(String phoneNumber, String message) {
        String phone = formatPhoneNumber(phoneNumber);
        log.info("Sending text message to {}", phone);

        Map<String, Object> payload = Map.of(
                "number", phone,
                "textMessage", Map.of("text", message));

        return webClient.post()
                .uri("/message/sendText/{instance}", instanceName)
                .bodyValue(payload)
                .retrieve()
                .bodyToMono(String.class)
                .map(response -> {
                    log.info("WhatsApp text sent: {}", response);
                    return true;
                })
                .onErrorResume(WebClientResponseException.class, e -> {
                    log.error("Evolution API error [{}]: {}", e.getStatusCode(), e.getResponseBodyAsString());
                    return Mono.error(new NotificationFailedException(
                            "Evolution API error: " + e.getStatusCode()));
                })
                .onErrorResume(Exception.class, e -> {
                    log.error("Connection error with Evolution API: {}", e.getMessage());
                    return Mono.error(new NotificationFailedException(
                            "Could not connect to Evolution API"));
                });
    }

    @Override
    public Mono<Boolean> sendImageMessage(String phoneNumber, String imageUrl, String caption) {
        if (imageUrl == null || imageUrl.isBlank()) {
            log.warn("No image URL provided, sending text only");
            return sendTextMessage(phoneNumber, caption);
        }

        String phone = formatPhoneNumber(phoneNumber);
        log.info("Sending image message to {} with URL", phone);

        Map<String, Object> mediaMessage = Map.of(
                "mediatype", "image",
                "mimetype", "image/jpeg",
                "media", imageUrl,
                "caption", caption);

        Map<String, Object> payload = Map.of(
                "number", phone,
                "mediaMessage", mediaMessage);

        return webClient.post()
                .uri("/message/sendMedia/{instance}", instanceName)
                .bodyValue(payload)
                .retrieve()
                .bodyToMono(String.class)
                .map(response -> {
                    log.info("WhatsApp image sent: {}", response);
                    return true;
                })
                .onErrorResume(WebClientResponseException.class, e -> {
                    log.error("Evolution API media error [{}]: {}", e.getStatusCode(), e.getResponseBodyAsString());
                    log.warn("Falling back to text-only message");
                    return sendTextMessage(phoneNumber, caption);
                })
                .onErrorResume(Exception.class, e -> {
                    log.error("Connection error (media): {}", e.getMessage());
                    return sendTextMessage(phoneNumber, caption);
                });
    }

    @Override
    public Mono<Boolean> isConnected() {
        return webClient.get()
                .uri("/instance/connectionState/{instance}", instanceName)
                .retrieve()
                .bodyToMono(String.class)
                .map(response -> response.contains("open"))
                .onErrorResume(e -> {
                    log.warn("Could not check connection state: {}", e.getMessage());
                    return Mono.just(false);
                });
    }

    private String formatPhoneNumber(String phone) {
        String cleaned = phone.replaceAll("[^0-9]", "");
        if (!cleaned.startsWith("51")) {
            cleaned = "51" + cleaned;
        }
        return cleaned + "@s.whatsapp.net";
    }
}
