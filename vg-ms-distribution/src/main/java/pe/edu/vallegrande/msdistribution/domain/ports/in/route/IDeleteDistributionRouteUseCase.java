package pe.edu.vallegrande.msdistribution.domain.ports.in.route;

import reactor.core.publisher.Mono;

public interface IDeleteDistributionRouteUseCase {
    Mono<Void> execute(String id, String deletedBy, String reason);
}
