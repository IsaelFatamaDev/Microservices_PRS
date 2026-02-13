package pe.edu.vallegrande.vgmsclaims.domain.ports.out;

import reactor.core.publisher.Mono;

import java.util.Map;

/**
 * Output port for the user service client
 */
public interface IUserServiceClient {
    
    /**
     * Gets user information by ID
     * @param userId identifier of the user
     * @return user data
     */
    Mono<Map<String, Object>> getUserById(String userId);
    
    /**
     * Gets user information by email
     * @param email email of the user
     * @return user data
     */
    Mono<Map<String, Object>> getUserByEmail(String email);
    
    /**
     * Checks if a user exists
     * @param userId identifier of the user
     * @return true if it exists
     */
    Mono<Boolean> existsUser(String userId);
    
    /**
     * Gets users of an organization
     * @param organizationId identifier of the organization
     * @return list of users
     */
    Mono<java.util.List<Map<String, Object>>> getUsersByOrganization(String organizationId);
}
