package pe.edu.vallegrande.vgmsclaims.domain.ports.in.incident;

import pe.edu.vallegrande.vgmsclaims.domain.models.Incident;
import reactor.core.publisher.Mono;

/**
 * Input port for the soft delete incident use case
 */
public interface IDeleteIncidentUseCase {
    
    /**
     * Logically deletes an incident (soft delete)
     * @param id identifier of the incident
     * @return the deleted incident
     */
    Mono<Incident> execute(String id);
}
