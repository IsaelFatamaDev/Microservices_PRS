package pe.edu.vallegrande.vgmsorganizations.infrastructure.persistence.repositories;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import pe.edu.vallegrande.vgmsorganizations.infrastructure.persistence.documents.FareDocument;
import reactor.core.publisher.Flux;

@Repository
public interface FareMongoRepository extends ReactiveMongoRepository<FareDocument, String> {
    Flux<FareDocument> findByRecordStatus(String recordStatus);

    Flux<FareDocument> findByOrganizationId(String organizationId);

    Flux<FareDocument> findByOrganizationIdAndRecordStatus(String organizationId, String recordStatus);

    Flux<FareDocument> findByOrganizationIdAndFareTypeAndRecordStatus(String organizationId, String fareType, String recordStatus);
}
