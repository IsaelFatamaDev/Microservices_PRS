package pe.edu.vallegrande.vgmsinfrastructure.infrastructure.persistence.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import pe.edu.vallegrande.vgmsinfrastructure.infrastructure.persistence.entity.WaterBoxEntity;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface WaterBoxR2dbcRepository extends ReactiveCrudRepository<WaterBoxEntity, String> {

    Flux<WaterBoxEntity> findByRecordStatus(String recordStatus);

    Flux<WaterBoxEntity> findByZoneId(String zoneId);

    Mono<Boolean> existsByBoxCode(String boxCode);
}
