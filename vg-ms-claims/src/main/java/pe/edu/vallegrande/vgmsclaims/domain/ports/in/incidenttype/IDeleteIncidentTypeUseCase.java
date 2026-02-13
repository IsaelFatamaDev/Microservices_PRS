package pe.edu.vallegrande.vgmsclaims.domain.ports.in.incidenttype;

import pe.edu.vallegrande.vgmsclaims.domain.models.IncidentType;
import reactor.core.publisher.Mono;

/**
 * Input port for the soft delete incident type use case
 */
public interface IDeleteIncidentTypeUseCase {
    
    /**
     * Logically deletes an incident type (soft delete)
     * @param id identifier of the incident type
     * @return the deleted incident type
     */
    Mono<IncidentType> execute(String id);
}
