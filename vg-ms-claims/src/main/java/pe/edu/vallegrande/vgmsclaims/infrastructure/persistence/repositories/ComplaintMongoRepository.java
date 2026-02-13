package pe.edu.vallegrande.vgmsclaims.infrastructure.persistence.repositories;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import pe.edu.vallegrande.vgmsclaims.infrastructure.persistence.documents.ComplaintDocument;
import reactor.core.publisher.Flux;

/**
 * Reactive MongoDB repository for complaints
 */
@Repository
public interface ComplaintMongoRepository extends ReactiveMongoRepository<ComplaintDocument, String> {
    
    Flux<ComplaintDocument> findByOrganizationId(String organizationId);
    
    Flux<ComplaintDocument> findByUserId(String userId);
    
    Flux<ComplaintDocument> findByStatus(String status);
    
    Flux<ComplaintDocument> findByCategoryId(String categoryId);
    
    Flux<ComplaintDocument> findByAssignedToUserId(String assignedToUserId);
    
    Flux<ComplaintDocument> findByRecordStatus(String recordStatus);
}
