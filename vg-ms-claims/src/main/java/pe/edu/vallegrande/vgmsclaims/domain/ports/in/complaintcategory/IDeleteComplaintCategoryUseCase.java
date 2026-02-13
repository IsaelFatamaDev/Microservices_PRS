package pe.edu.vallegrande.vgmsclaims.domain.ports.in.complaintcategory;

import pe.edu.vallegrande.vgmsclaims.domain.models.ComplaintCategory;
import reactor.core.publisher.Mono;

/**
 * Input port for the soft delete complaint category use case
 */
public interface IDeleteComplaintCategoryUseCase {
    
    /**
     * Logically deletes a complaint category (soft delete)
     * @param id identifier of the category
     * @return la category deleted
     */
    Mono<ComplaintCategory> execute(String id);
}
