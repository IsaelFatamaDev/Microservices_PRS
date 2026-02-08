package pe.edu.vallegrande.vgmsorganizations.domain.ports.in.zone;

import pe.edu.vallegrande.vgmsorganizations.domain.models.Zone;
import reactor.core.publisher.Mono;

public interface IUpdateZoneUseCase {
    Mono<Zone> execute(String id, Zone changes, String updatedBy);
}
