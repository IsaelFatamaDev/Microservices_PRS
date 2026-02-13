package pe.edu.vallegrande.vgmsinfrastructure.domain.ports.in;

import pe.edu.vallegrande.vgmsinfrastructure.domain.models.WaterBoxAssignment;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface IGetWaterBoxAssignmentUseCase {
    Mono<WaterBoxAssignment> findById(String id);
    Flux<WaterBoxAssignment> findAll();
    Flux<WaterBoxAssignment> findByWaterBoxId(String waterBoxId);
    Flux<WaterBoxAssignment> findByUserId(String userId);
}
