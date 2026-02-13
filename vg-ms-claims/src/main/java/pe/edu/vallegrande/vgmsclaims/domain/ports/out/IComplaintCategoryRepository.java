package pe.edu.vallegrande.vgmsclaims.domain.ports.out;

import pe.edu.vallegrande.vgmsclaims.domain.models.ComplaintCategory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Output port for the complaint category repository
 */
public interface IComplaintCategoryRepository {
    
    /**
     * Saves a complaint category
     * @param category category to save
     * @return the saved category
     */
    Mono<ComplaintCategory> save(ComplaintCategory category);
    
    /**
     * Finds a category by its ID
     * @param id identifier of the category
     * @return the found category
     */
    Mono<ComplaintCategory> findById(String id);
    
    /**
     * Gets all categories
     * @return list of categorys
     */
    Flux<ComplaintCategory> findAll();
    
    /**
     * Finds categories by organization
     * @param organizationId identifier of the organization
     * @return list of categorys
     */
    Flux<ComplaintCategory> findByOrganizationId(String organizationId);
    
    /**
     * Finds active categories by organization
     * @param organizationId identifier of the organization
     * @return list of categorys activas
     */
    Flux<ComplaintCategory> findActiveByOrganizationId(String organizationId);
    
    /**
     * Finds a category by code
     * @param categoryCode category code
     * @param organizationId identifier of the organization
     * @return the found category
     */
    Mono<ComplaintCategory> findByCategoryCodeAndOrganizationId(String categoryCode, String organizationId);
    
    /**
     * Checks if a category with the given code exists
     * @param categoryCode category code
     * @param organizationId identifier of the organization
     * @return true if it exists
     */
    Mono<Boolean> existsByCategoryCodeAndOrganizationId(String categoryCode, String organizationId);
}
