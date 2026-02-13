package pe.edu.vallegrande.vgmsinfrastructure.domain.ports.in;

import pe.edu.vallegrande.vgmsinfrastructure.domain.models.WaterBox;
import reactor.core.publisher.Mono;

public interface ISuspendWaterBoxUseCase {
    Mono<WaterBox> execute(String id, String suspendedBy);
}
