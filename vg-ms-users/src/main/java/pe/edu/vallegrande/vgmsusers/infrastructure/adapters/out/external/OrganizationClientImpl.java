package pe.edu.vallegrande.vgmsusers.infrastructure.adapters.out.external;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import pe.edu.vallegrande.vgmsusers.domain.ports.out.IOrganizationClient;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
@SuppressWarnings("unused")
public class OrganizationClientImpl implements IOrganizationClient {

    private final WebClient.Builder webClientBuilder;

    @Value("${webclient.services.organization.base-url}")
    private String organizationServiceUrl;

    @Value("${webclient.services.organization.skip-validation:false}")
    private boolean skipValidation;

    private static final String SERVICE_NAME = "organizationService";

    @Override
    @CircuitBreaker(name = SERVICE_NAME, fallbackMethod = "existsOrganizationFallback")
    @Retry(name = SERVICE_NAME)
    @TimeLimiter(name = SERVICE_NAME)
    public Mono<Boolean> existsOrganization(String organizationId) {
        log.debug("Checking if organization exists: {}", organizationId);

        return webClientBuilder.build()
                .get()
                .uri(organizationServiceUrl + "/api/v1/organizations/{id}/exists", organizationId)
                .retrieve()
                .bodyToMono(Boolean.class)
                .defaultIfEmpty(false);
    }

    @Override
    @CircuitBreaker(name = SERVICE_NAME, fallbackMethod = "existsZoneFallback")
    @Retry(name = SERVICE_NAME)
    @TimeLimiter(name = SERVICE_NAME)
    public Mono<Boolean> existsZone(String organizationId, String zoneId) {
        log.debug("Checking if zone {} exists in organization {}", zoneId, organizationId);

        return webClientBuilder.build()
                .get()
                .uri(organizationServiceUrl + "/api/v1/organizations/{orgId}/zones/{zoneId}/exists",
                        organizationId, zoneId)
                .retrieve()
                .bodyToMono(Boolean.class)
                .defaultIfEmpty(false);
    }

    @Override
    @CircuitBreaker(name = SERVICE_NAME, fallbackMethod = "existsStreetFallback")
    @Retry(name = SERVICE_NAME)
    @TimeLimiter(name = SERVICE_NAME)
    public Mono<Boolean> existsStreet(String zoneId, String streetId) {
        log.debug("Checking if street {} exists in zone {}", streetId, zoneId);

        return webClientBuilder.build()
                .get()
                .uri(organizationServiceUrl + "/api/v1/zones/{zoneId}/streets/{streetId}/exists",
                        zoneId, streetId)
                .retrieve()
                .bodyToMono(Boolean.class)
                .defaultIfEmpty(false);
    }

    @Override
    @CircuitBreaker(name = SERVICE_NAME, fallbackMethod = "validateHierarchyFallback")
    @Retry(name = SERVICE_NAME)
    @TimeLimiter(name = SERVICE_NAME)
    public Mono<Boolean> validateHierarchy(String organizationId, String zoneId, String streetId) {
        log.debug("Validating hierarchy: org={}, zone={}, street={}", organizationId, zoneId, streetId);

        if (skipValidation) {
            log.warn("DEV MODE: Skipping organization hierarchy validation");
            return Mono.just(true);
        }

        return webClientBuilder.build()
                .get()
                .uri(organizationServiceUrl
                        + "/api/v1/organizations/{orgId}/zones/{zoneId}/streets/{streetId}/validate",
                        organizationId, zoneId, streetId)
                .retrieve()
                .bodyToMono(Boolean.class)
                .defaultIfEmpty(false);
    }

    private Mono<Boolean> existsOrganizationFallback(String organizationId, Throwable t) {
        log.warn("Organization service unavailable, assuming exists: {}", organizationId);
        return Mono.just(true);
    }

    private Mono<Boolean> existsZoneFallback(String organizationId, String zoneId, Throwable t) {
        log.warn("Organization service unavailable, assuming zone exists: {}", zoneId);
        return Mono.just(true);
    }

    private Mono<Boolean> existsStreetFallback(String zoneId, String streetId, Throwable t) {
        log.warn("Organization service unavailable, assuming street exists: {}", streetId);
        return Mono.just(true);
    }

    private Mono<Boolean> validateHierarchyFallback(String orgId, String zoneId, String streetId, Throwable t) {
        log.warn("Organization service unavailable, skipping hierarchy validation");
        return Mono.just(true);
    }
}
