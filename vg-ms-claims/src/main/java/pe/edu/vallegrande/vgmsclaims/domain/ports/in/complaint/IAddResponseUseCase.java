package pe.edu.vallegrande.vgmsclaims.domain.ports.in.complaint;

import pe.edu.vallegrande.vgmsclaims.domain.models.Complaint;
import pe.edu.vallegrande.vgmsclaims.domain.models.ComplaintResponse;
import reactor.core.publisher.Mono;

/**
 * Input port for the add response to complaint use case
 */
public interface IAddResponseUseCase {
    
    /**
     * Adds a response to a complaint
     * @param complaintId identifier of the complaint
     * @param response response data
     * @return the complaint with the added response
     */
    Mono<Complaint> execute(String complaintId, ComplaintResponse response);
}
