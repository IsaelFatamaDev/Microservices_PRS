package pe.edu.vallegrande.vgmsusers.infrastructure.security;

import org.springframework.stereotype.Component;
import pe.edu.vallegrande.vgmsusers.domain.models.valueobjects.Role;
import pe.edu.vallegrande.vgmsusers.domain.ports.out.ISecurityContext;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.Set;

@Component
public class SecurityContextAdapter implements ISecurityContext {

    @Override
    public Mono<String> getCurrentUserId() {
        return getAuthenticatedUser().map(AuthenticatedUser::getUserId);
    }

    @Override
    public Mono<String> getCurrentOrganizationId() {
        return getAuthenticatedUser().map(AuthenticatedUser::getOrganizationId);
    }

    @Override
    public Mono<String> getCurrentUserEmail() {
        return getAuthenticatedUser().map(AuthenticatedUser::getEmail);
    }

    @Override
    public Mono<Set<Role>> getCurrentUserRoles() {
        return getAuthenticatedUser()
            .map(AuthenticatedUser::getRoles)
            .defaultIfEmpty(Collections.emptySet());
    }

    @Override
    public Mono<Boolean> isAuthenticated() {
        return getAuthenticatedUser()
            .map(AuthenticatedUser::isAuthenticated)
            .defaultIfEmpty(false);
    }

    public Mono<AuthenticatedUser> getAuthenticatedUser() {
        return Mono.deferContextual(ctx -> {
            if (ctx.hasKey(GatewayHeadersFilter.AUTHENTICATED_USER_KEY)) {
                return Mono.just(ctx.get(GatewayHeadersFilter.AUTHENTICATED_USER_KEY));
            }
            return Mono.just(AuthenticatedUser.anonymous());
        });
    }
}
