package pe.edu.vallegrande.vgmsauthentication.domain.ports.out;

import reactor.core.publisher.Mono;

import java.util.Set;

public interface ISecurityContext {

    Mono<String> getCurrentUserId();

    Mono<String> getCurrentOrganizationId();

    Mono<Set<String>> getCurrentUserRoles();

    Mono<Boolean> isAuthenticated();

    Mono<String> getCurrentUserEmail();
}
