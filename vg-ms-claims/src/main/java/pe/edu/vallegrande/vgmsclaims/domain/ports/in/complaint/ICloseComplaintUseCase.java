package pe.edu.vallegrande.vgmsclaims.domain.ports.in.complaint;

import pe.edu.vallegrande.vgmsclaims.domain.models.Complaint;
import reactor.core.publisher.Mono;

/**
 * Input port for the close complaint use case
 */
public interface ICloseComplaintUseCase {
    
    /**
     * Closes a complaint
     * @param id identifier of the complaint
     * @param satisfactionRating satisfaction rating (1-5)
     * @return the closed complaint
     */
    Mono<Complaint> execute(String id, Integer satisfactionRating);
}
