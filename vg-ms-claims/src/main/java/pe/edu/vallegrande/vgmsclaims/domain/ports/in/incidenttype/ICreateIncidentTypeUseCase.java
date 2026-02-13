package pe.edu.vallegrande.vgmsclaims.domain.ports.in.incidenttype;

import pe.edu.vallegrande.vgmsclaims.domain.models.IncidentType;
import reactor.core.publisher.Mono;

/**
 * Input port for the create incident type use case
 */
public interface ICreateIncidentTypeUseCase {
    
    /**
     * Creates a new incident type
     * @param incidentType data of the incident type to create
     * @return the created incident type
     */
    Mono<IncidentType> execute(IncidentType incidentType);
}
