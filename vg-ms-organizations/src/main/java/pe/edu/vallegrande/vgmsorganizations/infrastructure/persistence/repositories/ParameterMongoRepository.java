package pe.edu.vallegrande.vgmsorganizations.infrastructure.persistence.repositories;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import pe.edu.vallegrande.vgmsorganizations.infrastructure.persistence.documents.ParameterDocument;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface ParameterMongoRepository extends ReactiveMongoRepository<ParameterDocument, String> {
    Flux<ParameterDocument> findByRecordStatus(String recordStatus);

    Flux<ParameterDocument> findByOrganizationId(String organizationId);

    Flux<ParameterDocument> findByOrganizationIdAndRecordStatus(String organizationId, String recordStatus);

    Mono<Boolean> existsByParameterTypeAndOrganizationId(String parameterType, String organizationId);
}