package pe.edu.vallegrande.vgmswaterquality.domain.ports.out;

import reactor.core.publisher.Mono;

public interface ISecurityContext {
    Mono<String> getCurrentUserId();

    Mono<String> getCurrentOrganizationId();

    Mono<Boolean> isSuperAdmin();

    Mono<Boolean> isAdmin();

    Mono<Boolean> belongsToOrganization(String organizationId);
}
