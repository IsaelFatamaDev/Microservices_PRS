package pe.edu.vallegrande.vgmsorganizations.domain.ports.in.fare;

import pe.edu.vallegrande.vgmsorganizations.domain.models.Fare;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface IGetFareUseCase {
    Mono<Fare> findById(String id);

    Flux<Fare> findAll();

    Flux<Fare> findAllActive();

    Flux<Fare> findByOrganizationId(String organizationId);

    Flux<Fare> findActiveByOrganizationId(String organizationId);
}
