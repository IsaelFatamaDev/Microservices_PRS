package pe.edu.vallegrande.vgmsclaims.domain.ports.in.incident;

import pe.edu.vallegrande.vgmsclaims.domain.models.Incident;
import reactor.core.publisher.Mono;

/**
 * Input port for the update incident use case
 */
public interface IUpdateIncidentUseCase {
    
    /**
     * Updates an incident existing
     * @param id identifier of the incident
     * @param incident updated incident data
     * @return the updated incident
     */
    Mono<Incident> execute(String id, Incident incident);
}
