package pe.edu.vallegrande.vgmsclaims.domain.ports.in.incidenttype;

import pe.edu.vallegrande.vgmsclaims.domain.models.IncidentType;
import reactor.core.publisher.Mono;

/**
 * Input port for the update incident type use case
 */
public interface IUpdateIncidentTypeUseCase {
    
    /**
     * Updates an incident type existing
     * @param id identifier of the incident type
     * @param incidentType updated data of the incident type
     * @return the updated incident type
     */
    Mono<IncidentType> execute(String id, IncidentType incidentType);
}
