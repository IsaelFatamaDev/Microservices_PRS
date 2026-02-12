package pe.edu.vallegrande.msdistribution.domain.ports.in.route;

import pe.edu.vallegrande.msdistribution.domain.models.DistributionRoute;
import reactor.core.publisher.Mono;

public interface ICreateDistributionRouteUseCase {
    Mono<DistributionRoute> execute(DistributionRoute route, String createdBy);
}
