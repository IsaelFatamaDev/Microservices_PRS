package pe.edu.vallegrande.vgmsinfrastructure.domain.ports.out;

import pe.edu.vallegrande.vgmsinfrastructure.domain.models.WaterBox;
import pe.edu.vallegrande.vgmsinfrastructure.domain.models.valueobjects.RecordStatus;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface IWaterBoxRepository {
    Mono<WaterBox> save(WaterBox waterBox);
    Mono<WaterBox> findById(String id);
    Flux<WaterBox> findAll();
    Flux<WaterBox> findByRecordStatus(RecordStatus status);
    Flux<WaterBox> findByZoneId(String zoneId);
    Mono<Boolean> existsByBoxCode(String boxCode);
}
