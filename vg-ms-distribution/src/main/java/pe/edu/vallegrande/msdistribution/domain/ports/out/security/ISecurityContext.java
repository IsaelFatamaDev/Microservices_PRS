package pe.edu.vallegrande.msdistribution.domain.ports.out.security;

import reactor.core.publisher.Mono;

/**
 * Puerto de salida para el contexto de seguridad.
 * Define el contrato para acceder a información del usuario autenticado en flujos reactivos.
 * 
 * @version 1.0
 */
public interface ISecurityContext {
    
    /**
     * Obtiene el ID del usuario actualmente autenticado.
     * 
     * @return Mono con el ID del usuario o vacío si no hay usuario autenticado
     */
    Mono<String> getCurrentUserId();
    
    /**
     * Obtiene el ID de la organización del usuario autenticado.
     * 
     * @return Mono con el ID de la organización o vacío
     */
    Mono<String> getCurrentOrganizationId();
    
    /**
     * Obtiene el email del usuario autenticado.
     * 
     * @return Mono con el email del usuario o vacío
     */
    Mono<String> getCurrentUserEmail();
    
    /**
     * Verifica si el usuario actual es Super Admin.
     * 
     * @return Mono con true si es Super Admin, false en caso contrario
     */
    Mono<Boolean> isSuperAdmin();
    
    /**
     * Verifica si el usuario actual es Admin (incluye Super Admin).
     * 
     * @return Mono con true si es Admin, false en caso contrario
     */
    Mono<Boolean> isAdmin();
    
    /**
     * Verifica si el usuario pertenece a una organización específica.
     * Los Super Admins tienen acceso a todas las organizaciones.
     * 
     * @param organizationId ID de la organización a verificar
     * @return Mono con true si pertenece, false en caso contrario
     */
    Mono<Boolean> belongsToOrganization(String organizationId);
}
