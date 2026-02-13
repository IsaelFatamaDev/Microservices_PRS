package pe.edu.vallegrande.vgmsclaims.domain.ports.in.complaint;

import pe.edu.vallegrande.vgmsclaims.domain.models.Complaint;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Input port for the get complaints use case
 */
public interface IGetComplaintUseCase {
    
    /**
     * Gets all complaints
     * @return list of complaints
     */
    Flux<Complaint> findAll();
    
    /**
     * Gets a complaint by its ID
     * @param id identifier of the complaint
     * @return the found complaint
     */
    Mono<Complaint> findById(String id);
    
    /**
     * Gets complaints by organization
     * @param organizationId identifier of the organization
     * @return list of complaints of the organization
     */
    Flux<Complaint> findByOrganizationId(String organizationId);
    
    /**
     * Gets complaints by user
     * @param userId identifier of the user
     * @return list of complaints of the user
     */
    Flux<Complaint> findByUserId(String userId);
    
    /**
     * Gets complaints by status
     * @param status status of the complaint
     * @return list of complaints with that status
     */
    Flux<Complaint> findByStatus(String status);
}
