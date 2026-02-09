package pe.edu.vallegrande.vgmsorganizations.domain.ports.out.street;

import pe.edu.vallegrande.vgmsorganizations.domain.models.Street;
import pe.edu.vallegrande.vgmsorganizations.domain.models.valueobjects.RecordStatus;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface IStreetRepository {
    Mono<Street> save(Street street);

    Mono<Street> update(Street street);

    Mono<Street> findById(String id);

    Flux<Street> findAll();

    Flux<Street> findByRecordStatus(RecordStatus status);

    Flux<Street> findByZoneId(String zoneId);

    Flux<Street> findByZoneIdAndRecordStatus(String zoneId, RecordStatus status);

    Mono<Boolean> existsByStreetNameAndZoneId(String streetName, String zoneId);

    Mono<Void> deleteById(String id);

}
