package pe.edu.vallegrande.vgmsauthentication.infrastructure.security;

import lombok.Builder;
import lombok.Getter;

import java.util.Set;

@Getter
@Builder
public class AuthenticatedUser {

    private final String userId;
    private final String email;
    private final String organizationId;
    private final Set<String> roles;

    public boolean hasRole(String role) {
        return roles != null && roles.contains(role);
    }

    public boolean hasAnyRole(String... requiredRoles) {
        if (roles == null) return false;
        for (String role : requiredRoles) {
            if (roles.contains(role)) return true;
        }
        return false;
    }

    public boolean isAdmin() {
        return hasRole("ROLE_ADMIN") || hasRole("ADMIN");
    }
}