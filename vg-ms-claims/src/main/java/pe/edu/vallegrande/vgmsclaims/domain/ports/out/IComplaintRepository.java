package pe.edu.vallegrande.vgmsclaims.domain.ports.out;

import pe.edu.vallegrande.vgmsclaims.domain.models.Complaint;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Output port for the complaint repository
 */
public interface IComplaintRepository {
    
    /**
     * Saves a complaint
     * @param complaint complaint to save
     * @return the saved complaint
     */
    Mono<Complaint> save(Complaint complaint);
    
    /**
     * Finds a complaint by its ID
     * @param id identifier of the complaint
     * @return the found complaint
     */
    Mono<Complaint> findById(String id);
    
    /**
     * Gets all complaints
     * @return list of complaints
     */
    Flux<Complaint> findAll();
    
    /**
     * Finds complaints by organization
     * @param organizationId identifier of the organization
     * @return list of complaints
     */
    Flux<Complaint> findByOrganizationId(String organizationId);
    
    /**
     * Finds complaints by user
     * @param userId identifier of the user
     * @return list of complaints
     */
    Flux<Complaint> findByUserId(String userId);
    
    /**
     * Finds complaints by status
     * @param status status of the complaint
     * @return list of complaints
     */
    Flux<Complaint> findByStatus(String status);
    
    /**
     * Finds complaints by category
     * @param categoryId identifier of the category
     * @return list of complaints
     */
    Flux<Complaint> findByCategoryId(String categoryId);
    
    /**
     * Deletes una complaint by its ID
     * @param id identifier of the complaint
     * @return void
     */
    Mono<Void> deleteById(String id);
    
    /**
     * Checks if a complaint with the given ID exists
     * @param id identifier of the complaint
     * @return true if it exists
     */
    Mono<Boolean> existsById(String id);
}
