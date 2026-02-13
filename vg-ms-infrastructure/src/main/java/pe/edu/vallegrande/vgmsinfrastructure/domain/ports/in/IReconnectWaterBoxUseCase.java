package pe.edu.vallegrande.vgmsinfrastructure.domain.ports.in;

import pe.edu.vallegrande.vgmsinfrastructure.domain.models.WaterBox;
import reactor.core.publisher.Mono;

public interface IReconnectWaterBoxUseCase {
    Mono<WaterBox> execute(String id, String reconnectedBy);
}
