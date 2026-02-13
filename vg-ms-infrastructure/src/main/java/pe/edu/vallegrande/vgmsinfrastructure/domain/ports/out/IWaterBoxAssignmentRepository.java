package pe.edu.vallegrande.vgmsinfrastructure.domain.ports.out;

import pe.edu.vallegrande.vgmsinfrastructure.domain.models.WaterBoxAssignment;
import pe.edu.vallegrande.vgmsinfrastructure.domain.models.valueobjects.AssignmentStatus;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface IWaterBoxAssignmentRepository {
    Mono<WaterBoxAssignment> save(WaterBoxAssignment assignment);
    Mono<WaterBoxAssignment> findById(String id);
    Flux<WaterBoxAssignment> findAll();
    Flux<WaterBoxAssignment> findByWaterBoxId(String waterBoxId);
    Flux<WaterBoxAssignment> findByUserId(String userId);
    Mono<WaterBoxAssignment> findActiveByWaterBoxId(String waterBoxId);
}
