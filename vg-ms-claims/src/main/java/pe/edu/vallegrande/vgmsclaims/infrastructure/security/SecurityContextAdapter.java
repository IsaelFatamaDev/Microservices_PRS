package pe.edu.vallegrande.vgmsclaims.infrastructure.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import pe.edu.vallegrande.vgmsclaims.domain.ports.out.ISecurityContext;
import reactor.core.publisher.Mono;

/**
 * Adapter implementing the security port using gateway context.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SecurityContextAdapter implements ISecurityContext {

    @Override
    public Mono<AuthenticatedUser> getCurrentUser() {
        return GatewayHeadersFilter.getCurrentUser()
                .doOnNext(user -> log.debug("Current user: {}", user.getUserId()));
    }

    @Override
    public Mono<String> getCurrentUserId() {
        return GatewayHeadersFilter.getCurrentUserId()
                .doOnNext(userId -> log.debug("Current user ID: {}", userId));
    }

    @Override
    public Mono<String> getCurrentOrganizationId() {
        return GatewayHeadersFilter.getCurrentOrganizationId()
                .doOnNext(orgId -> log.debug("Current organization: {}", orgId));
    }

    @Override
    public Mono<Boolean> hasRole(String role) {
        return GatewayHeadersFilter.getCurrentUser()
                .map(user -> user.getRoles() != null && user.getRoles().contains(role))
                .defaultIfEmpty(false);
    }

    @Override
    public Mono<Boolean> isAdmin() {
        return GatewayHeadersFilter.getCurrentUser()
                .map(AuthenticatedUser::isAdmin)
                .defaultIfEmpty(false);
    }
}
