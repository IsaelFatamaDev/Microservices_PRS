package pe.edu.vallegrande.vgmsclaims.domain.ports.in.complaint;

import pe.edu.vallegrande.vgmsclaims.domain.models.Complaint;
import reactor.core.publisher.Mono;

/**
 * Input port for the soft delete complaint use case
 */
public interface IDeleteComplaintUseCase {
    
    /**
     * Logically deletes a complaint (soft delete)
     * @param id identifier of the complaint
     * @return the deleted complaint
     */
    Mono<Complaint> execute(String id);
}
