package pe.edu.vallegrande.vgmsorganizations.domain.ports.in.organization;

import pe.edu.vallegrande.vgmsorganizations.domain.models.Organization;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface IGetOrganizationUseCase {
    Mono<Organization> findById(String id);

    Flux<Organization> findAll();

    Flux<Organization> findAllActive();
}
