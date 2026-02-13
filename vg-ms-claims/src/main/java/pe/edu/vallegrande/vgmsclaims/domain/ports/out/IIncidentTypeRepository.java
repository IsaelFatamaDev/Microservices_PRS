package pe.edu.vallegrande.vgmsclaims.domain.ports.out;

import pe.edu.vallegrande.vgmsclaims.domain.models.IncidentType;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Output port for the incident type repository
 */
public interface IIncidentTypeRepository {
    
    /**
     * Saves an incident type
     * @param incidentType incident type to save
     * @return the saved incident type
     */
    Mono<IncidentType> save(IncidentType incidentType);
    
    /**
     * Finds an incident type by its ID
     * @param id identifier of the incident type
     * @return the found incident type
     */
    Mono<IncidentType> findById(String id);
    
    /**
     * Gets all incident types
     * @return list of incident types
     */
    Flux<IncidentType> findAll();
    
    /**
     * Finds incident types by organization
     * @param organizationId identifier of the organization
     * @return list of incident types
     */
    Flux<IncidentType> findByOrganizationId(String organizationId);
    
    /**
     * Finds active incident types by organization
     * @param organizationId identifier of the organization
     * @return list of incident types activos
     */
    Flux<IncidentType> findActiveByOrganizationId(String organizationId);
    
    /**
     * Finds an incident type by code
     * @param typeCode incident type code
     * @param organizationId identifier of the organization
     * @return the found incident type
     */
    Mono<IncidentType> findByTypeCodeAndOrganizationId(String typeCode, String organizationId);
    
    /**
     * Checks if an incident type with the given code exists
     * @param typeCode incident type code
     * @param organizationId identifier of the organization
     * @return true if it exists
     */
    Mono<Boolean> existsByTypeCodeAndOrganizationId(String typeCode, String organizationId);
}
