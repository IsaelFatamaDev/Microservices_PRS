package pe.edu.vallegrande.vgmsclaims.domain.ports.in.incident;

import pe.edu.vallegrande.vgmsclaims.domain.models.Incident;
import reactor.core.publisher.Mono;

/**
 * Input port for the assign incident use case
 */
public interface IAssignIncidentUseCase {

    /**
     * Assigns a technician to an incident
     * 
     * @param incidentId identifier of the incident
     * @param userId     identifier of the technician to assign
     * @return the incident with the assignment
     */
    Mono<Incident> execute(String incidentId, String userId);
}
