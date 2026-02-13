package pe.edu.vallegrande.vgmsclaims.domain.ports.in.incidenttype;

import pe.edu.vallegrande.vgmsclaims.domain.models.IncidentType;
import reactor.core.publisher.Mono;

/**
 * Input port for the restore incident type use case
 */
public interface IRestoreIncidentTypeUseCase {
    
    /**
     * Restores a deleted incident type
     * @param id identifier of the incident type
     * @return the restored incident type
     */
    Mono<IncidentType> execute(String id);
}
