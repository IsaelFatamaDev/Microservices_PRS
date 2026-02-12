package pe.edu.vallegrande.msdistribution.domain.ports.in.route;

import reactor.core.publisher.Mono;

public interface IRestoreDistributionRouteUseCase {
    Mono<Void> execute(String id, String restoredBy);
}
