package pe.edu.vallegrande.vgmsusers.domain.ports.out;

import pe.edu.vallegrande.vgmsusers.domain.models.valueobjects.Role;
import reactor.core.publisher.Mono;

import java.util.Set;

public interface ISecurityContext {

    Mono<String> getCurrentUserId();

    Mono<String> getCurrentOrganizationId();

    Mono<Set<Role>> getCurrentUserRoles();

    Mono<Boolean> isAuthenticated();
}
