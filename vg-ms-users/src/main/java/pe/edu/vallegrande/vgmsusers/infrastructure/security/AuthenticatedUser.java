package pe.edu.vallegrande.vgmsusers.infrastructure.security;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import pe.edu.vallegrande.vgmsusers.domain.models.valueobjects.Role;

import java.util.Collections;
import java.util.Set;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthenticatedUser {

    private String userId;
    private String organizationId;
    private String email;
    private Set<Role> roles;
    private boolean authenticated;

    public static AuthenticatedUser anonymous() {
        return AuthenticatedUser.builder()
            .userId("anonymous")
            .organizationId(null)
            .roles(Collections.emptySet())
            .authenticated(false)
            .build();
    }

    public boolean isSuperAdmin() {
        return roles.contains(Role.SUPER_ADMIN);
    }

    public boolean isAdmin() {
        return roles.contains(Role.ADMIN);
    }

    public boolean hasAdminPrivileges() {
        return isSuperAdmin() || isAdmin();
    }

    public boolean canCreateUsers() {
        return isSuperAdmin() || isAdmin();
    }

    public boolean canCreateRole(Role targetRole) {
        if (isSuperAdmin()) {
            return targetRole == Role.ADMIN;
        }
        if (isAdmin()) {
            return targetRole == Role.CLIENT || targetRole == Role.OPERATOR;
        }
        return false;
    }

    public boolean canAssignMultipleRoles() {
        return isSuperAdmin();
    }

    public boolean belongsToOrganization(String orgId) {
        return organizationId != null && organizationId.equals(orgId);
    }
}
