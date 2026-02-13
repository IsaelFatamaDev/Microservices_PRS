package pe.edu.vallegrande.vgmsclaims.domain.ports.in.complaint;

import pe.edu.vallegrande.vgmsclaims.domain.models.Complaint;
import reactor.core.publisher.Mono;

/**
 * Input port for el create use case complaint
 */
public interface ICreateComplaintUseCase {

    /**
     * Creates a new complaint
     * 
     * @param complaint complaint data to create
     * @return the created complaint
     */
    Mono<Complaint> execute(Complaint complaint);
}
