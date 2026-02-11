package pe.edu.vallegrande.vgmsnotifications.infrastructure.adapters.out.rest;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@SuppressWarnings({ "null", "unused" })
public class OrganizationApiClient {

    private static final String CIRCUIT_BREAKER_NAME = "organizationsApi";

    private final WebClient webClient;

    public OrganizationApiClient(
            WebClient.Builder webClientBuilder,
            @Value("${app.organizations-api.base-url}") String baseUrl) {
        this.webClient = webClientBuilder.baseUrl(baseUrl).build();
    }

    @CircuitBreaker(name = CIRCUIT_BREAKER_NAME, fallbackMethod = "orgFallback")
    public Mono<OrganizationInfo> getOrganizationInfo(String organizationId) {
        if (organizationId == null || organizationId.isBlank()) {
            return Mono.just(OrganizationInfo.empty());
        }

        return webClient.get()
                .uri("/api/v1/organizations/{id}", organizationId)
                .retrieve()
                .bodyToMono(OrganizationApiResponse.class)
                .map(response -> new OrganizationInfo(
                        response.data().organizationName(),
                        response.data().logoUrl()))
                .doOnNext(info -> log.debug("Organization info for {}: {}", organizationId, info.name()));
    }

    private Mono<OrganizationInfo> orgFallback(String organizationId, Throwable t) {
        log.warn("Circuit breaker fallback for org {}: {}", organizationId, t.getMessage());
        return Mono.just(OrganizationInfo.empty());
    }

    public record OrganizationInfo(String name, String logoUrl) {
        public static OrganizationInfo empty() {
            return new OrganizationInfo("JASS", null);
        }

        public boolean hasLogo() {
            return logoUrl != null && !logoUrl.isBlank();
        }
    }

    private record OrganizationApiResponse(OrganizationData data) {
    }

    private record OrganizationData(String organizationName, String logoUrl) {
    }
}
