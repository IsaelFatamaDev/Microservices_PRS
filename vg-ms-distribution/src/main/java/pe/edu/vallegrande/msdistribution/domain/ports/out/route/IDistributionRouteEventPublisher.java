package pe.edu.vallegrande.msdistribution.domain.ports.out.route;

import pe.edu.vallegrande.msdistribution.domain.models.DistributionRoute;
import reactor.core.publisher.Mono;

import java.util.Map;

public interface IDistributionRouteEventPublisher {
    Mono<Void> publishRouteCreated(DistributionRoute route, String createdBy);
    Mono<Void> publishRouteUpdated(DistributionRoute route, Map<String, Object> changedFields, String updatedBy);
    Mono<Void> publishRouteDeleted(String routeId, String deletedBy, String reason);
    Mono<Void> publishRouteRestored(String routeId, String restoredBy);
}
