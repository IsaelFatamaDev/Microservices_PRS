package pe.edu.vallegrande.vgmsorganizations.infrastructure.persistence.repositories;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import pe.edu.vallegrande.vgmsorganizations.infrastructure.persistence.documents.StreetDocument;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface StreetMongoRepository extends ReactiveMongoRepository<StreetDocument, String> {
    Flux<StreetDocument> findByRecordStatus(String recordStatus);

    Flux<StreetDocument> findByZoneId(String zoneId);

    Flux<StreetDocument> findByZoneIdAndRecordStatus(String zoneId, String recordStatus);

    Mono<Boolean> existsByStreetNameAndZoneId(String streetName, String zoneId);
}