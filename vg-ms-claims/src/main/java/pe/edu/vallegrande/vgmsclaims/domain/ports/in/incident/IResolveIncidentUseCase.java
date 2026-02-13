package pe.edu.vallegrande.vgmsclaims.domain.ports.in.incident;

import pe.edu.vallegrande.vgmsclaims.domain.models.Incident;
import pe.edu.vallegrande.vgmsclaims.domain.models.IncidentResolution;
import reactor.core.publisher.Mono;

/**
 * Input port for the resolve incident use case
 */
public interface IResolveIncidentUseCase {
    
    /**
     * Resolves an incident
     * @param incidentId identifier of the incident
     * @param resolution data of the resolution
     * @return the resolved incident
     */
    Mono<Incident> execute(String incidentId, IncidentResolution resolution);
}
