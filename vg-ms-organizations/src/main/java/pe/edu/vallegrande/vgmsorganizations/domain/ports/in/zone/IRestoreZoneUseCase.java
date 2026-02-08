package pe.edu.vallegrande.vgmsorganizations.domain.ports.in.zone;

import pe.edu.vallegrande.vgmsorganizations.domain.models.Zone;
import reactor.core.publisher.Mono;

public interface IRestoreZoneUseCase {
    Mono<Zone> execute(String id, String restoredBy);
}
