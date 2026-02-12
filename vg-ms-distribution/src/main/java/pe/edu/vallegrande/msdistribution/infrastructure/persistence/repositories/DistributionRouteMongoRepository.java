package pe.edu.vallegrande.msdistribution.infrastructure.persistence.repositories;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import pe.edu.vallegrande.msdistribution.infrastructure.persistence.documents.DistributionRouteDocument;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface DistributionRouteMongoRepository extends ReactiveMongoRepository<DistributionRouteDocument, String> {
    Flux<DistributionRouteDocument> findAllByRecordStatus(String recordStatus);
    Flux<DistributionRouteDocument> findByOrganizationId(String organizationId);
}
