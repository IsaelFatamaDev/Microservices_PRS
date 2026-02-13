package pe.edu.vallegrande.vgmsclaims.domain.ports.in.complaintcategory;

import pe.edu.vallegrande.vgmsclaims.domain.models.ComplaintCategory;
import reactor.core.publisher.Mono;

/**
 * Input port for the update complaint category use case
 */
public interface IUpdateComplaintCategoryUseCase {
    
    /**
     * Updates an existing complaint category
     * @param id identifier of the category
     * @param category data updated of the category
     * @return the updated category
     */
    Mono<ComplaintCategory> execute(String id, ComplaintCategory category);
}
