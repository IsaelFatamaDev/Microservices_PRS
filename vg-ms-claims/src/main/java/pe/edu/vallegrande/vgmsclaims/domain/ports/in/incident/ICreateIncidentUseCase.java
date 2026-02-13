package pe.edu.vallegrande.vgmsclaims.domain.ports.in.incident;

import pe.edu.vallegrande.vgmsclaims.domain.models.Incident;
import reactor.core.publisher.Mono;

/**
 * Input port for the create incident use case
 */
public interface ICreateIncidentUseCase {
    
    /**
     * Creates a new incident
     * @param incident incident data to create
     * @return the created incident
     */
    Mono<Incident> execute(Incident incident);
}
