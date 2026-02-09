package pe.edu.vallegrande.vgmsorganizations.domain.ports.out.zone;

import pe.edu.vallegrande.vgmsorganizations.domain.models.Zone;
import pe.edu.vallegrande.vgmsorganizations.domain.models.valueobjects.RecordStatus;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface IZoneRepository {
    Mono<Zone> save(Zone zone);

    Mono<Zone> update(Zone zone);

    Mono<Zone> findById(String id);

    Flux<Zone> findAll();

    Flux<Zone> findByRecordStatus(RecordStatus status);

    Flux<Zone> findByOrganizationId(String organizationId);

    Flux<Zone> findByOrganizationIdAndRecordStatus(String organizationId, RecordStatus status);

    Mono<Boolean> existsByZoneNameAndOrganizationId(String zoneName, String organizationId);

    Mono<Void> deleteById(String id);

}
