package pe.edu.vallegrande.vgmsorganizations.domain.ports.out.street;

import pe.edu.vallegrande.vgmsorganizations.domain.models.Street;
import reactor.core.publisher.Mono;

import java.util.Map;

public interface IStreetEventPublisher {

    Mono<Void> publishStreetCreated(Street street, String createdBy);

    Mono<Void> publishStreetUpdated(Street street, Map<String, Object> changedFields, String updatedBy);

    Mono<Void> publishStreetDeleted(String streetId, String zoneId, String reason, String deletedBy);

    Mono<Void> publishStreetRestored(String streetId, String zoneId, String restoredBy);
}
