package pe.edu.vallegrande.vgmsinfrastructure.domain.ports.in;

import pe.edu.vallegrande.vgmsinfrastructure.domain.models.WaterBox;
import reactor.core.publisher.Mono;

public interface ICreateWaterBoxUseCase {
    Mono<WaterBox> execute(WaterBox waterBox, String createdBy);
}
