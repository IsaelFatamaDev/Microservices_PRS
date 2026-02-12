package pe.edu.vallegrande.msdistribution.infrastructure.persistence.repositories;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import pe.edu.vallegrande.msdistribution.infrastructure.persistence.documents.DistributionScheduleDocument;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface DistributionScheduleMongoRepository extends ReactiveMongoRepository<DistributionScheduleDocument, String> {
    Flux<DistributionScheduleDocument> findAllByRecordStatus(String recordStatus);
    Flux<DistributionScheduleDocument> findByOrganizationId(String organizationId);
}
