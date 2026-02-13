package pe.edu.vallegrande.vgmsclaims.domain.ports.in.complaintcategory;

import pe.edu.vallegrande.vgmsclaims.domain.models.ComplaintCategory;
import reactor.core.publisher.Mono;

/**
 * Input port for the restore complaint category use case
 */
public interface IRestoreComplaintCategoryUseCase {
    
    /**
     * Restores a deleted complaint category
     * @param id identifier of the category
     * @return la category restaurada
     */
    Mono<ComplaintCategory> execute(String id);
}
