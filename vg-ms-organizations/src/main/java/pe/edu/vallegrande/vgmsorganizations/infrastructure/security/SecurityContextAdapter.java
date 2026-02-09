package pe.edu.vallegrande.vgmsorganizations.infrastructure.security;

import org.springframework.stereotype.Component;
import pe.edu.vallegrande.vgmsorganizations.domain.ports.out.ISecurityContext;
import reactor.core.publisher.Mono;

@Component
public class SecurityContextAdapter implements ISecurityContext {

    @Override
    public Mono<String> getCurrentUserId() {
        return Mono.deferContextual(ctx -> {
            if (ctx.hasKey(GatewayHeadersFilter.AUTHENTICATED_USER_KEY)) {
                AuthenticatedUser user = ctx.get(GatewayHeadersFilter.AUTHENTICATED_USER_KEY);
                return Mono.justOrEmpty(user.getUserId());
            }
            return Mono.empty();
        });
    }

    @Override
    public Mono<String> getCurrentOrganizationId() {
        return Mono.deferContextual(ctx -> {
            if (ctx.hasKey(GatewayHeadersFilter.AUTHENTICATED_USER_KEY)) {
                AuthenticatedUser user = ctx.get(GatewayHeadersFilter.AUTHENTICATED_USER_KEY);
                return Mono.justOrEmpty(user.getOrganizationId());
            }
            return Mono.empty();
        });
    }

    @Override
    public Mono<Boolean> isSuperAdmin() {
        return Mono.deferContextual(ctx -> {
            if (ctx.hasKey(GatewayHeadersFilter.AUTHENTICATED_USER_KEY)) {
                AuthenticatedUser user = ctx.get(GatewayHeadersFilter.AUTHENTICATED_USER_KEY);
                return Mono.just(user.isSuperAdmin());
            }
            return Mono.just(false);
        });
    }

    @Override
    public Mono<Boolean> isAdmin() {
        return Mono.deferContextual(ctx -> {
            if (ctx.hasKey(GatewayHeadersFilter.AUTHENTICATED_USER_KEY)) {
                AuthenticatedUser user = ctx.get(GatewayHeadersFilter.AUTHENTICATED_USER_KEY);
                return Mono.just(user.isAdmin());
            }
            return Mono.just(false);
        });
    }

    @Override
    public Mono<Boolean> belongsToOrganization(String organizationId) {
        return Mono.deferContextual(ctx -> {
            if (ctx.hasKey(GatewayHeadersFilter.AUTHENTICATED_USER_KEY)) {
                AuthenticatedUser user = ctx.get(GatewayHeadersFilter.AUTHENTICATED_USER_KEY);
                return Mono.just(user.belongsToOrganization(organizationId));
            }
            return Mono.just(false);
        });
    }
}
