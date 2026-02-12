package pe.edu.vallegrande.msdistribution.infrastructure.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import pe.edu.vallegrande.msdistribution.domain.ports.out.security.ISecurityContext;
import reactor.core.publisher.Mono;

/**
 * Implementación del puerto ISecurityContext.
 * Accede al usuario autenticado almacenado en el contexto reactivo
 * por el GatewayHeadersFilter.
 */
@Slf4j
@Component
public class SecurityContextAdapter implements ISecurityContext {
    
    @Override
    public Mono<String> getCurrentUserId() {
        return getAuthenticatedUser()
                .map(AuthenticatedUser::getUserId)
                .doOnNext(userId -> log.debug("Retrieved current user ID: {}", userId));
    }
    
    @Override
    public Mono<String> getCurrentOrganizationId() {
        return getAuthenticatedUser()
                .map(AuthenticatedUser::getOrganizationId)
                .doOnNext(orgId -> log.debug("Retrieved current organization ID: {}", orgId));
    }
    
    @Override
    public Mono<String> getCurrentUserEmail() {
        return getAuthenticatedUser()
                .map(AuthenticatedUser::getEmail)
                .doOnNext(email -> log.debug("Retrieved current user email: {}", email));
    }
    
    @Override
    public Mono<Boolean> isSuperAdmin() {
        return getAuthenticatedUser()
                .map(AuthenticatedUser::isSuperAdmin)
                .doOnNext(isSuperAdmin -> log.debug("Is super admin: {}", isSuperAdmin));
    }
    
    @Override
    public Mono<Boolean> isAdmin() {
        return getAuthenticatedUser()
                .map(AuthenticatedUser::isAdmin)
                .doOnNext(isAdmin -> log.debug("Is admin: {}", isAdmin));
    }
    
    @Override
    public Mono<Boolean> belongsToOrganization(String organizationId) {
        return getAuthenticatedUser()
                .map(user -> user.belongsToOrganization(organizationId))
                .doOnNext(belongs -> log.debug("Belongs to organization {}: {}", organizationId, belongs));
    }
    
    /**
     * Obtiene el usuario autenticado desde el contexto reactivo.
     * @return Mono con el AuthenticatedUser
     */
    private Mono<AuthenticatedUser> getAuthenticatedUser() {
        return Mono.deferContextual(ctx -> {
            if (ctx.hasKey(GatewayHeadersFilter.AUTHENTICATED_USER_KEY)) {
                AuthenticatedUser user = ctx.get(GatewayHeadersFilter.AUTHENTICATED_USER_KEY);
                return Mono.just(user);
            }
            log.warn("No authenticated user found in reactive context");
            return Mono.empty();
        });
    }
}
