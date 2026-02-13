package pe.edu.vallegrande.vgmsinfrastructure.infrastructure.persistence.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import pe.edu.vallegrande.vgmsinfrastructure.infrastructure.persistence.entity.WaterBoxTransferEntity;
import reactor.core.publisher.Flux;

@Repository
public interface WaterBoxTransferR2dbcRepository extends ReactiveCrudRepository<WaterBoxTransferEntity, String> {

    Flux<WaterBoxTransferEntity> findByWaterBoxId(String waterBoxId);
}
