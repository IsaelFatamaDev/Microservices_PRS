package pe.edu.vallegrande.vgmsusers.domain.services;

import pe.edu.vallegrande.vgmsusers.domain.exceptions.BusinessRuleException;
import pe.edu.vallegrande.vgmsusers.domain.models.valueobjects.Role;

import java.util.Set;

public class UserAuthorizationService {

    private UserAuthorizationService() {}

    public static void validateCanCreateUserWithRole(Set<Role> creatorRoles, Role targetRole) {
        if (creatorRoles.isEmpty()) {
            throw new BusinessRuleException("Authentication required to create users");
        }

        boolean isSuperAdmin = creatorRoles.contains(Role.SUPER_ADMIN);
        boolean isAdmin = creatorRoles.contains(Role.ADMIN);

        if (!isSuperAdmin && !isAdmin) {
            throw new BusinessRuleException("Only SUPER_ADMIN or ADMIN can create users");
        }

        if (isSuperAdmin) {
            if (targetRole != Role.ADMIN) {
                throw new BusinessRuleException("SUPER_ADMIN can only create ADMIN users");
            }
            return;
        }

        if (isAdmin) {
            if (targetRole != Role.CLIENT && targetRole != Role.OPERATOR) {
                throw new BusinessRuleException("ADMIN can only create CLIENT or OPERATOR users");
            }
            return;
        }
    }

    public static void validateCanAccessOrganization(String userOrgId, String targetOrgId) {
        if (userOrgId == null || targetOrgId == null) {
            return;
        }
        if (!userOrgId.equals(targetOrgId)) {
            throw new BusinessRuleException("You can only access users from your organization");
        }
    }

    public static boolean canViewAllOrganizations(Set<Role> roles) {
        return roles.contains(Role.SUPER_ADMIN);
    }
}
