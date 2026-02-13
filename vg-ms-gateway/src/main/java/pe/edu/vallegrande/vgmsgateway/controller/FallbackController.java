package pe.edu.vallegrande.vgmsgateway.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/fallback")
public class FallbackController {

    @RequestMapping("/auth")
    public Mono<ResponseEntity<Map<String, Object>>> authFallback() {
        return buildFallbackResponse("authentication", "Servicio de autenticación no disponible");
    }

    @RequestMapping("/users")
    public Mono<ResponseEntity<Map<String, Object>>> usersFallback() {
        return buildFallbackResponse("users", "Servicio de usuarios no disponible");
    }

    @RequestMapping("/organizations")
    public Mono<ResponseEntity<Map<String, Object>>> organizationsFallback() {
        return buildFallbackResponse("organizations", "Servicio de organizaciones no disponible");
    }

    @RequestMapping("/notifications")
    public Mono<ResponseEntity<Map<String, Object>>> notificationsFallback() {
        return buildFallbackResponse("notifications", "Servicio de notificaciones no disponible");
    }

    @RequestMapping("/distribution")
    public Mono<ResponseEntity<Map<String, Object>>> distributionFallback() {
        return buildFallbackResponse("distribution", "Servicio de distribución no disponible");
    }

    @RequestMapping("/infrastructure")
    public Mono<ResponseEntity<Map<String, Object>>> infrastructureFallback() {
        return buildFallbackResponse("infrastructure", "Servicio de infraestructura no disponible");
    }

    @RequestMapping("/commercial")
    public Mono<ResponseEntity<Map<String, Object>>> commercialFallback() {
        return buildFallbackResponse("commercial", "Servicio comercial no disponible");
    }

    @RequestMapping("/claims")
    public Mono<ResponseEntity<Map<String, Object>>> claimsFallback() {
        return buildFallbackResponse("claims", "Servicio de reclamos e incidencias no disponible");
    }

    @RequestMapping("/inventory")
    public Mono<ResponseEntity<Map<String, Object>>> inventoryFallback() {
        return buildFallbackResponse("inventory", "Servicio de inventario y compras no disponible");
    }

    private Mono<ResponseEntity<Map<String, Object>>> buildFallbackResponse(String service, String message) {
        Map<String, Object> response = Map.of(
                "success", false,
                "message", message,
                "service", service,
                "timestamp", LocalDateTime.now().toString());
        return Mono.just(ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(response));
    }
}
