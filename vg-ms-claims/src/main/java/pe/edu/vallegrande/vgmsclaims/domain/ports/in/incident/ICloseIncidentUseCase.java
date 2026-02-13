package pe.edu.vallegrande.vgmsclaims.domain.ports.in.incident;

import pe.edu.vallegrande.vgmsclaims.domain.models.Incident;
import reactor.core.publisher.Mono;

/**
 * Input port for the close incident use case
 */
public interface ICloseIncidentUseCase {
    
    /**
     * Closes an incident
     * @param id identifier of the incident
     * @return the closed incident
     */
    Mono<Incident> execute(String id);
}
