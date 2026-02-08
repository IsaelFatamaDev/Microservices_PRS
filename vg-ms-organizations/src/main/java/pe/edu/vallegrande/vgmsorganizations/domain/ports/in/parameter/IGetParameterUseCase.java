package pe.edu.vallegrande.vgmsorganizations.domain.ports.in.parameter;

import pe.edu.vallegrande.vgmsorganizations.domain.models.Parameter;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface IGetParameterUseCase {
    Mono<Parameter> findById(String id);

    Flux<Parameter> findAll();

    Flux<Parameter> findAllActive();

    Flux<Parameter> findByOrganizationId(String organizationId);

    Flux<Parameter> findActiveByOrganizationId(String organizationId);
}
