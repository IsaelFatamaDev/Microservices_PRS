package pe.edu.vallegrande.vgmsclaims.domain.ports.in.complaintcategory;

import pe.edu.vallegrande.vgmsclaims.domain.models.ComplaintCategory;
import reactor.core.publisher.Mono;

/**
 * Input port for the create complaint category use case
 */
public interface ICreateComplaintCategoryUseCase {

    /**
     * Creates a new complaint category
     * 
     * @param category category data to create
     * @return the created category
     */
    Mono<ComplaintCategory> execute(ComplaintCategory category);
}
