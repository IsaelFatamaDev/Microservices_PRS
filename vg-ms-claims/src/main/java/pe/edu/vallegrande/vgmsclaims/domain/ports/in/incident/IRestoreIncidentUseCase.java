package pe.edu.vallegrande.vgmsclaims.domain.ports.in.incident;

import pe.edu.vallegrande.vgmsclaims.domain.models.Incident;
import reactor.core.publisher.Mono;

/**
 * Input port for the restore incident use case
 */
public interface IRestoreIncidentUseCase {

    /**
     * Restores a deleted incident
     * 
     * @param id identifier of the incident
     * @return the restored incident
     */
    Mono<Incident> execute(String id);
}
