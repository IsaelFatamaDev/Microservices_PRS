package pe.edu.vallegrande.msdistribution.infrastructure.persistence.repositories;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import pe.edu.vallegrande.msdistribution.infrastructure.persistence.documents.DistributionProgramDocument;
import reactor.core.publisher.Flux;

public interface DistributionProgramMongoRepository extends ReactiveMongoRepository<DistributionProgramDocument, String> {
    Flux<DistributionProgramDocument> findAllByRecordStatus(String recordStatus);
    Flux<DistributionProgramDocument> findByOrganizationId(String organizationId);
}
