package pe.edu.vallegrande.vgmsclaims.infrastructure.security;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Represents the authenticated user information extracted from gateway headers.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthenticatedUser {

    private String userId;
    private String organizationId;
    private String email;
    private List<String> roles;

    /**
     * Checks if user is super administrator.
     */
    public boolean isSuperAdmin() {
        return roles != null && roles.contains("SUPER_ADMIN");
    }

    /**
     * Checks if user is administrator.
     */
    public boolean isAdmin() {
        return roles != null && (roles.contains("ADMIN") || roles.contains("SUPER_ADMIN"));
    }

    /**
     * Checks if user belongs to an organization.
     */
    public boolean belongsToOrganization(String orgId) {
        if (isSuperAdmin())
            return true;
        return organizationId != null && organizationId.equals(orgId);
    }

    /**
     * Checks if user can create a specific role.
     */
    public boolean canCreateRole(String role) {
        if (isSuperAdmin())
            return true;
        if (isAdmin()) {
            return !"SUPER_ADMIN".equals(role) && !"ADMIN".equals(role);
        }
        return false;
    }
}
