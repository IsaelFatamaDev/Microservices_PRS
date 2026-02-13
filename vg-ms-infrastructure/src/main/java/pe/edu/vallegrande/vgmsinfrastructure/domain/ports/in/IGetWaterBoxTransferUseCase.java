package pe.edu.vallegrande.vgmsinfrastructure.domain.ports.in;

import pe.edu.vallegrande.vgmsinfrastructure.domain.models.WaterBoxTransfer;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface IGetWaterBoxTransferUseCase {
    Mono<WaterBoxTransfer> findById(String id);
    Flux<WaterBoxTransfer> findAll();
    Flux<WaterBoxTransfer> findByWaterBoxId(String waterBoxId);
}
