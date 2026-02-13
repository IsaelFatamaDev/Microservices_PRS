package pe.edu.vallegrande.vgmscommercial.domain.ports.out;

import reactor.core.publisher.Mono;

public interface ISecurityContext {
    Mono<String> getCurrentUserId();
    Mono<String> getCurrentOrganizationId();
    Mono<Boolean> hasRole(String role);
    Mono<Boolean> isSuperAdmin();
}