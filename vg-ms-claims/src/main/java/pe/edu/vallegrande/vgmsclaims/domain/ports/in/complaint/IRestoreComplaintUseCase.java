package pe.edu.vallegrande.vgmsclaims.domain.ports.in.complaint;

import pe.edu.vallegrande.vgmsclaims.domain.models.Complaint;
import reactor.core.publisher.Mono;

/**
 * Input port for the restore complaint use case
 */
public interface IRestoreComplaintUseCase {
    
    /**
     * Restores a deleted complaint
     * @param id identifier of the complaint
     * @return the restored complaint
     */
    Mono<Complaint> execute(String id);
}
