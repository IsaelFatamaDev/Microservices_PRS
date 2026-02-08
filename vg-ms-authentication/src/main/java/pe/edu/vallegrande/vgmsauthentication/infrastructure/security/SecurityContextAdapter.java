package pe.edu.vallegrande.vgmsauthentication.infrastructure.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import pe.edu.vallegrande.vgmsauthentication.domain.ports.out.ISecurityContext;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.Set;

@Slf4j
@Component
public class SecurityContextAdapter implements ISecurityContext {

    @Override
    public Mono<String> getCurrentUserId() {
        return getAuthenticatedUser()
            .map(AuthenticatedUser::getUserId);
    }

    @Override
    public Mono<String> getCurrentOrganizationId() {
        return getAuthenticatedUser()
            .map(AuthenticatedUser::getOrganizationId);
    }

    @Override
    public Mono<Set<String>> getCurrentUserRoles() {
        return getAuthenticatedUser()
            .map(AuthenticatedUser::getRoles)
            .defaultIfEmpty(Collections.emptySet());
    }

    @Override
    public Mono<Boolean> isAuthenticated() {
        return getAuthenticatedUser()
            .map(user -> true)
            .defaultIfEmpty(false);
    }

    @Override
    public Mono<String> getCurrentUserEmail() {
        return getAuthenticatedUser()
            .map(AuthenticatedUser::getEmail);
    }

    private Mono<AuthenticatedUser> getAuthenticatedUser() {
        return Mono.deferContextual(ctx -> {
            if (ctx.hasKey(GatewayHeadersFilter.AUTHENTICATED_USER_KEY)) {
                return Mono.just(ctx.get(GatewayHeadersFilter.AUTHENTICATED_USER_KEY));
            }
            return Mono.empty();
        });
    }
}
