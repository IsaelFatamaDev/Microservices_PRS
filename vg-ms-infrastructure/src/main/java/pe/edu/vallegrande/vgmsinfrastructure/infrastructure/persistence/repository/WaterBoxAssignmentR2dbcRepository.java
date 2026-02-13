package pe.edu.vallegrande.vgmsinfrastructure.infrastructure.persistence.repository;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import pe.edu.vallegrande.vgmsinfrastructure.infrastructure.persistence.entity.WaterBoxAssignmentEntity;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface WaterBoxAssignmentR2dbcRepository extends ReactiveCrudRepository<WaterBoxAssignmentEntity, String> {

    Flux<WaterBoxAssignmentEntity> findByWaterBoxId(String waterBoxId);

    Flux<WaterBoxAssignmentEntity> findByUserId(String userId);

    @Query("SELECT * FROM water_box_assignments WHERE water_box_id = :waterBoxId AND assignment_status = 'ACTIVE' LIMIT 1")
    Mono<WaterBoxAssignmentEntity> findActiveByWaterBoxId(String waterBoxId);
}
