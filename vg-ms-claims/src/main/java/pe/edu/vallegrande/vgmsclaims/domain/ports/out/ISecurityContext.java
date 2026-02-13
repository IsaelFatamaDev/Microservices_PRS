package pe.edu.vallegrande.vgmsclaims.domain.ports.out;

import pe.edu.vallegrande.vgmsclaims.infrastructure.security.AuthenticatedUser;
import reactor.core.publisher.Mono;

/**
 * Output port for the security context
 */
public interface ISecurityContext {

    /**
     * Gets the current authenticated user
     * 
     * @return the authenticated user
     */
    Mono<AuthenticatedUser> getCurrentUser();

    /**
     * Gets the current authenticated user ID
     * 
     * @return the user ID
     */
    Mono<String> getCurrentUserId();

    /**
     * Gets the organization ID of the current user
     * 
     * @return the organization ID
     */
    Mono<String> getCurrentOrganizationId();

    /**
     * Checks if the current user has a specific role
     * 
     * @param role role to verify
     * @return true if has the role
     */
    Mono<Boolean> hasRole(String role);

    /**
     * Checks if the current user is an administrator
     * 
     * @return true if administrator
     */
    Mono<Boolean> isAdmin();
}
