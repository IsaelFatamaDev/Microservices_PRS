package pe.edu.vallegrande.vgmsclaims.domain.ports.in.incident;

import pe.edu.vallegrande.vgmsclaims.domain.models.Incident;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Input port for the get incidents use case
 */
public interface IGetIncidentUseCase {
    
    /**
     * Gets all incidents
     * @return list of incidents
     */
    Flux<Incident> findAll();
    
    /**
     * Gets an incident by its ID
     * @param id identifier of the incident
     * @return the found incident
     */
    Mono<Incident> findById(String id);
    
    /**
     * Gets incidents by organization
     * @param organizationId identifier of the organization
     * @return list of incidents of the organization
     */
    Flux<Incident> findByOrganizationId(String organizationId);
    
    /**
     * Gets incidents by zone
     * @param zoneId identifier of the zone
     * @return list of incidents of the zone
     */
    Flux<Incident> findByZoneId(String zoneId);
    
    /**
     * Gets incidents by severity
     * @param severity severity of the incident
     * @return list of incidents with that severity
     */
    Flux<Incident> findBySeverity(String severity);
    
    /**
     * Gets incidents by status
     * @param status status of the incident
     * @return list of incidents with that status
     */
    Flux<Incident> findByStatus(String status);
}
