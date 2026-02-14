package pe.edu.vallegrande.vgmswaterquality.infrastructure.persistence.repositories;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import pe.edu.vallegrande.vgmswaterquality.infrastructure.persistence.documents.QualityTestDocument;
import reactor.core.publisher.Flux;

@Repository
public interface QualityTestMongoRepository extends ReactiveMongoRepository<QualityTestDocument, String> {
    Flux<QualityTestDocument> findByRecordStatus(String recordStatus);
    Flux<QualityTestDocument> findByOrganizationIdAndRecordStatus(String organizationId, String recordStatus);
    Flux<QualityTestDocument> findByOrganizationId(String organizationId);
}
