package pe.edu.vallegrande.vgmsclaims.domain.ports.out;

import pe.edu.vallegrande.vgmsclaims.domain.models.Incident;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Output port for the incident repository
 */
public interface IIncidentRepository {
    
    /**
     * Saves an incident
     * @param incident incident to save
     * @return the saved incident
     */
    Mono<Incident> save(Incident incident);
    
    /**
     * Finds an incident by its ID
     * @param id identifier of the incident
     * @return the found incident
     */
    Mono<Incident> findById(String id);
    
    /**
     * Gets all incidents
     * @return list of incidents
     */
    Flux<Incident> findAll();
    
    /**
     * Finds incidents by organization
     * @param organizationId identifier of the organization
     * @return list of incidents
     */
    Flux<Incident> findByOrganizationId(String organizationId);
    
    /**
     * Finds incidents by zone
     * @param zoneId identifier of the zone
     * @return list of incidents
     */
    Flux<Incident> findByZoneId(String zoneId);
    
    /**
     * Finds incidents by severity
     * @param severity severity of the incident
     * @return list of incidents
     */
    Flux<Incident> findBySeverity(String severity);
    
    /**
     * Finds incidents by status
     * @param status status of the incident
     * @return list of incidents
     */
    Flux<Incident> findByStatus(String status);
    
    /**
     * Finds incidents by type
     * @param incidentTypeId identifier of the type
     * @return list of incidents
     */
    Flux<Incident> findByIncidentTypeId(String incidentTypeId);
    
    /**
     * Finds incidents by assigned user
     * @param assignedToUserId identifier of the assigned user
     * @return list of incidents
     */
    Flux<Incident> findByAssignedToUserId(String assignedToUserId);
    
    /**
     * Finds incidents by resolution status
     * @param resolved resolution status
     * @return list of incidents
     */
    Flux<Incident> findByResolved(Boolean resolved);
    
    /**
     * Finds incidents by record status
     * @param recordStatus status of the record
     * @return list of incidents
     */
    Flux<Incident> findByRecordStatus(String recordStatus);
    
    /**
     * Deletes an incident by its ID
     * @param id identifier of the incident
     * @return void
     */
    Mono<Void> deleteById(String id);
    
    /**
     * Checks if an incident with the given ID exists
     * @param id identifier of the incident
     * @return true if it exists
     */
    Mono<Boolean> existsById(String id);
}
