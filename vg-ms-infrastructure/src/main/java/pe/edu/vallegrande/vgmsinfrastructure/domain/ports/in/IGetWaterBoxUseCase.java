package pe.edu.vallegrande.vgmsinfrastructure.domain.ports.in;

import pe.edu.vallegrande.vgmsinfrastructure.domain.models.WaterBox;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface IGetWaterBoxUseCase {
    Mono<WaterBox> findById(String id);
    Flux<WaterBox> findAll();
    Flux<WaterBox> findAllActive();
    Flux<WaterBox> findByZoneId(String zoneId);
}
