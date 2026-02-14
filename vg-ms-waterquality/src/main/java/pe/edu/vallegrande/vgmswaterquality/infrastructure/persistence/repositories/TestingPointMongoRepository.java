package pe.edu.vallegrande.vgmswaterquality.infrastructure.persistence.repositories;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import pe.edu.vallegrande.vgmswaterquality.infrastructure.persistence.documents.TestingPointDocument;
import reactor.core.publisher.Flux;
@Repository
public interface TestingPointMongoRepository extends ReactiveMongoRepository<TestingPointDocument , String > {
    Flux<TestingPointDocument> findByRecordStatus(String recordStatus);
    Flux<TestingPointDocument> findByOrganizationId(String organizationId);
    Flux<TestingPointDocument> findByOrganizationIdAndRecordStatus(String organizationId , String recordStatus);
}

