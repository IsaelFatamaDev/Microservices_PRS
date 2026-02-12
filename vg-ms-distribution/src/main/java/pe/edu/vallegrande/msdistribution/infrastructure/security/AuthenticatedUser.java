package pe.edu.vallegrande.msdistribution.infrastructure.security;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Modelo del usuario autenticado extraído de los headers del API Gateway.
 * El Gateway valida el JWT con Keycloak y envía la información en headers HTTP.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthenticatedUser {
    
    /**
     * ID del usuario autenticado (extraído del header X-User-Id)
     */
    private String userId;
    
    /**
     * ID de la organización del usuario (extraído del header X-Organization-Id)
     */
    private String organizationId;
    
    /**
     * Email del usuario (extraído del header X-User-Email)
     */
    private String email;
    
    /**
     * Lista de roles del usuario (extraído del header X-Roles, separados por coma)
     */
    private List<String> roles;
    
    /**
     * Verifica si el usuario es SUPER_ADMIN.
     * @return true si tiene el rol SUPER_ADMIN
     */
    public boolean isSuperAdmin() {
        return roles != null && roles.contains("SUPER_ADMIN");
    }
    
    /**
     * Verifica si el usuario es ADMIN o SUPER_ADMIN.
     * @return true si tiene el rol ADMIN o SUPER_ADMIN
     */
    public boolean isAdmin() {
        return roles != null && (roles.contains("ADMIN") || isSuperAdmin());
    }
    
    /**
     * Verifica si el usuario pertenece a la organización especificada.
     * Los SUPER_ADMIN tienen acceso a todas las organizaciones.
     * @param orgId ID de la organización a verificar
     * @return true si pertenece a la organización o es SUPER_ADMIN
     */
    public boolean belongsToOrganization(String orgId) {
        if (isSuperAdmin()) {
            return true;
        }
        return organizationId != null && organizationId.equals(orgId);
    }
}
