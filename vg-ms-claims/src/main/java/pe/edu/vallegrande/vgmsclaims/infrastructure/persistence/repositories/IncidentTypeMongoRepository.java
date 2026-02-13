package pe.edu.vallegrande.vgmsclaims.infrastructure.persistence.repositories;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import pe.edu.vallegrande.vgmsclaims.infrastructure.persistence.documents.IncidentTypeDocument;
import reactor.core.publisher.Flux;

/**
 * Reactive MongoDB repository for incident types
 */
@Repository
public interface IncidentTypeMongoRepository extends ReactiveMongoRepository<IncidentTypeDocument, String> {
    
    Flux<IncidentTypeDocument> findByOrganizationId(String organizationId);
    
    Flux<IncidentTypeDocument> findByRecordStatus(String recordStatus);
}
