package pe.edu.vallegrande.vgmsclaims.domain.ports.out;

import pe.edu.vallegrande.vgmsclaims.domain.models.ComplaintResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Output port for the complaint response repository
 */
public interface IComplaintResponseRepository {
    
    /**
     * Saves a complaint response
     * @param response response to save
     * @return la response saved
     */
    Mono<ComplaintResponse> save(ComplaintResponse response);
    
    /**
     * Finds a response by its ID
     * @param id identifier of the response
     * @return la response found
     */
    Mono<ComplaintResponse> findById(String id);
    
    /**
     * Gets all responses of a complaint
     * @param complaintId identifier of the complaint
     * @return list of responses
     */
    Flux<ComplaintResponse> findByComplaintId(String complaintId);
    
    /**
     * Gets responses by user
     * @param userId identifier of the user who responded
     * @return list of responses
     */
    Flux<ComplaintResponse> findByUserId(String userId);
    
    /**
     * Counts the responses of a complaint
     * @param complaintId identifier of the complaint
     * @return number of responses
     */
    Mono<Long> countByComplaintId(String complaintId);
    
    /**
     * Deletes all responses of a complaint
     * @param complaintId identifier of the complaint
     * @return void
     */
    Mono<Void> deleteByComplaintId(String complaintId);
}
