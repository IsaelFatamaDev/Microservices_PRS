package pe.edu.vallegrande.vgmsclaims.infrastructure.persistence.repositories;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import pe.edu.vallegrande.vgmsclaims.infrastructure.persistence.documents.ComplaintCategoryDocument;
import reactor.core.publisher.Flux;

/**
 * Reactive MongoDB repository for complaint categories
 */
@Repository
public interface ComplaintCategoryMongoRepository extends ReactiveMongoRepository<ComplaintCategoryDocument, String> {
    
    Flux<ComplaintCategoryDocument> findByOrganizationId(String organizationId);
    
    Flux<ComplaintCategoryDocument> findByRecordStatus(String recordStatus);
}
