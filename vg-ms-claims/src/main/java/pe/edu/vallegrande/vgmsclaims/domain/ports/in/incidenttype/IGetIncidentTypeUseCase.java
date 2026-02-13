package pe.edu.vallegrande.vgmsclaims.domain.ports.in.incidenttype;

import pe.edu.vallegrande.vgmsclaims.domain.models.IncidentType;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Input port for the get incident types use case
 */
public interface IGetIncidentTypeUseCase {
    
    /**
     * Gets all incident types
     * @return list of incident types
     */
    Flux<IncidentType> findAll();
    
    /**
     * Gets an incident type by its ID
     * @param id identifier of the incident type
     * @return the found incident type
     */
    Mono<IncidentType> findById(String id);
    
    /**
     * Gets incident types by organization
     * @param organizationId identifier of the organization
     * @return list of incident types of the organization
     */
    Flux<IncidentType> findByOrganizationId(String organizationId);
    
    /**
     * Gets active incident types by organization
     * @param organizationId identifier of the organization
     * @return list of incident types activos
     */
    Flux<IncidentType> findActiveByOrganizationId(String organizationId);
}
