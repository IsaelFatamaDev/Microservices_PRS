package pe.edu.vallegrande.vgmsorganizations.infrastructure.security;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthenticatedUser {

    private String userId;
    private String organizationId;
    private String email;
    private List<String> roles;

    public boolean isSuperAdmin() {
        return roles != null && roles.contains("SUPER_ADMIN");
    }

    public boolean isAdmin() {
        return roles != null && (roles.contains("ADMIN") || roles.contains("SUPER_ADMIN"));
    }

    public boolean belongsToOrganization(String orgId) {
        if (isSuperAdmin()) return true;
        return organizationId != null && organizationId.equals(orgId);
    }

    public boolean canCreateRole(String role) {
        if (isSuperAdmin()) return true;
        if (isAdmin()) {
            return !"SUPER_ADMIN".equals(role) && !"ADMIN".equals(role);
        }
        return false;
    }
}
