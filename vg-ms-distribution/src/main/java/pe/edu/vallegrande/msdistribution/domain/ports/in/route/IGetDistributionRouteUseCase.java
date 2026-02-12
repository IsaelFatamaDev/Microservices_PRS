package pe.edu.vallegrande.msdistribution.domain.ports.in.route;

import pe.edu.vallegrande.msdistribution.domain.models.DistributionRoute;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface IGetDistributionRouteUseCase {
    Mono<DistributionRoute> findById(String id);
    Flux<DistributionRoute> findAll();
    Flux<DistributionRoute> findAllActive();
}
