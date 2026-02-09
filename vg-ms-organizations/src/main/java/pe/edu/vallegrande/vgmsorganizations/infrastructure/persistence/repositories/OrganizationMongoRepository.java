package pe.edu.vallegrande.vgmsorganizations.infrastructure.persistence.repositories;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import pe.edu.vallegrande.vgmsorganizations.infrastructure.persistence.documents.OrganizationDocument;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface OrganizationMongoRepository extends ReactiveMongoRepository<OrganizationDocument, String> {
    Flux<OrganizationDocument> findByRecordStatus(String recordStatus);

    Mono<OrganizationDocument> findByOrganizationName(String organizationName);

    Mono<Boolean> existsByOrganizationName(String organizationName);
}
