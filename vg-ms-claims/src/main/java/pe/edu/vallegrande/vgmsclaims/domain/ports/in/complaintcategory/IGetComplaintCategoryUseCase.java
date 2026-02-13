package pe.edu.vallegrande.vgmsclaims.domain.ports.in.complaintcategory;

import pe.edu.vallegrande.vgmsclaims.domain.models.ComplaintCategory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Input port for the get complaint categories use case
 */
public interface IGetComplaintCategoryUseCase {
    
    /**
     * Gets all categories de complaint
     * @return list of categorys
     */
    Flux<ComplaintCategory> findAll();
    
    /**
     * Gets a category by its ID
     * @param id identifier of the category
     * @return the found category
     */
    Mono<ComplaintCategory> findById(String id);
    
    /**
     * Gets categories by organization
     * @param organizationId identifier of the organization
     * @return list of categorys of the organization
     */
    Flux<ComplaintCategory> findByOrganizationId(String organizationId);
    
    /**
     * Gets active categories by organization
     * @param organizationId identifier of the organization
     * @return list of categorys activas
     */
    Flux<ComplaintCategory> findActiveByOrganizationId(String organizationId);
}
