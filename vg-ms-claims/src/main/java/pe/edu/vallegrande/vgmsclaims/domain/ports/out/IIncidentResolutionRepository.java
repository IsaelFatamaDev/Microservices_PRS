package pe.edu.vallegrande.vgmsclaims.domain.ports.out;

import pe.edu.vallegrande.vgmsclaims.domain.models.IncidentResolution;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Output port for the incident resolution repository
 */
public interface IIncidentResolutionRepository {
    
    /**
     * Saves an incident resolution
     * @param resolution resolution to save
     * @return the saved resolution
     */
    Mono<IncidentResolution> save(IncidentResolution resolution);
    
    /**
     * Finds a resolution by its ID
     * @param id identifier of the resolution
     * @return the found resolution
     */
    Mono<IncidentResolution> findById(String id);
    
    /**
     * Gets the resolution of an incident
     * @param incidentId identifier of the incident
     * @return the resolution of the incident
     */
    Mono<IncidentResolution> findByIncidentId(String incidentId);
    
    /**
     * Gets resolutions by technician
     * @param technicianId identifier of the technician
     * @return list of resoluciones
     */
    Flux<IncidentResolution> findByTechnicianId(String technicianId);
    
    /**
     * Gets resolutions by resolution type
     * @param resolutionType resolution type
     * @return list of resoluciones
     */
    Flux<IncidentResolution> findByResolutionType(String resolutionType);
    
    /**
     * Checks if a resolution exists for an incident
     * @param incidentId identifier of the incident
     * @return true if it exists
     */
    Mono<Boolean> existsByIncidentId(String incidentId);
    
    /**
     * Deletes the resolution of an incident
     * @param incidentId identifier of the incident
     * @return void
     */
    Mono<Void> deleteByIncidentId(String incidentId);
}
