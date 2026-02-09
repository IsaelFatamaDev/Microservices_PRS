package pe.edu.vallegrande.vgmsorganizations.infrastructure.persistence.repositories;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import pe.edu.vallegrande.vgmsorganizations.infrastructure.persistence.documents.ZoneDocument;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface ZoneMongoRepository extends ReactiveMongoRepository<ZoneDocument, String> {
    Flux<ZoneDocument> findByRecordStatus(String recordStatus);

    Flux<ZoneDocument> findByOrganizationId(String organizationId);

    Flux<ZoneDocument> findByOrganizationIdAndRecordStatus(String organizationId, String recordStatus);

    Mono<Boolean> existsByZoneNameAndOrganizationId(String zoneName, String organizationId);
}
