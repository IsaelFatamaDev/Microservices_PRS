package pe.edu.vallegrande.vgmswaterquality.infrastructure.security;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticatedUser {
     private String userId;
     private String role;
     private String organizationId;

     public boolean isSuperAdmin() {
          return "SUPER_ADMIN".equals(role);
     }

     public boolean isAdmin() {
          return "ADMIN".equals(role) || isSuperAdmin();
     }

     public boolean belongsToOrganization(String orgId) {
          return isSuperAdmin() || (organizationId != null && organizationId.equals(orgId));
     }
}
