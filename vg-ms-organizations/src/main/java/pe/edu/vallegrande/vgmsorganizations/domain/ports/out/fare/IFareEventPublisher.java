package pe.edu.vallegrande.vgmsorganizations.domain.ports.out.fare;

import pe.edu.vallegrande.vgmsorganizations.domain.models.Fare;
import reactor.core.publisher.Mono;

import java.util.Map;

public interface IFareEventPublisher {
    Mono<Void> publishFareCreated(Fare fare, String createdBy);

    Mono<Void> publishFareUpdated(Fare fare, Map<String, Object> changedFields, String updatedBy);

    Mono<Void> publishFareDeleted(String fareId, String organizationId, String deletedBy, String reason);

    Mono<Void> publishFareRestore(String fareId, String organizationId, String restoredBy);
}
