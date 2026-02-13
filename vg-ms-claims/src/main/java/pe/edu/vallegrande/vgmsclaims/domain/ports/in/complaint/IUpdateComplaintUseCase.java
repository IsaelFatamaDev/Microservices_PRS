package pe.edu.vallegrande.vgmsclaims.domain.ports.in.complaint;

import pe.edu.vallegrande.vgmsclaims.domain.models.Complaint;
import reactor.core.publisher.Mono;

/**
 * Input port for the update complaint use case
 */
public interface IUpdateComplaintUseCase {
    
    /**
     * Updates an existing complaint
     * @param id identifier of the complaint
     * @param complaint updated complaint data
     * @return the updated complaint
     */
    Mono<Complaint> execute(String id, Complaint complaint);
}
