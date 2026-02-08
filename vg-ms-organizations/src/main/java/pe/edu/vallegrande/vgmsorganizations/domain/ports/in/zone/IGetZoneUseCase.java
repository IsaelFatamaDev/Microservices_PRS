package pe.edu.vallegrande.vgmsorganizations.domain.ports.in.zone;

import pe.edu.vallegrande.vgmsorganizations.domain.models.Zone;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface IGetZoneUseCase {
    Mono<Zone> findById(String id);

    Flux<Zone> findAll();

    Flux<Zone> findAllActive();

    Flux<Zone> findByOrganizationId(String organizationId);

    Flux<Zone> findActiveByOrganizationId(String organizationId);

}
