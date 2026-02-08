package pe.edu.vallegrande.vgmsorganizations.domain.ports.in.street;

import pe.edu.vallegrande.vgmsorganizations.domain.models.Street;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface IGetStreetUseCase {
    Mono<Street> findById(String id);

    Flux<Street> findAll();

    Flux<Street> findAllActive();

    Flux<Street> findByZoneId(String zoneId);

    Flux<Street> findActiveByZoneId(String zoneId);
}
