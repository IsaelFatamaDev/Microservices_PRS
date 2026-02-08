package pe.edu.vallegrande.vgmsorganizations.domain.ports.out.zone;

import pe.edu.vallegrande.vgmsorganizations.domain.models.Zone;
import reactor.core.publisher.Mono;

import java.util.Map;

public interface IZoneEventPublisher {
    Mono<Void> publishZoneCreated(Zone zone, String createdBy);

    Mono<Void> publishZoneUpdated(Zone zone, Map<String, Object> changedFields, String updatedBy);

    Mono<Void> publishZoneDeleted(String zoneId, String organizationId, String deletedBy, String reason);

    Mono<Void> publishZoneRestored(String zoneId, String organizationId, String restoredBy);
}
