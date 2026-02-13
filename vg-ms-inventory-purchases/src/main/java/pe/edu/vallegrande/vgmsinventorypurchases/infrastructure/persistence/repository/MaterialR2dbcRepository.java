package pe.edu.vallegrande.vgmsinventorypurchases.infrastructure.persistence.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import pe.edu.vallegrande.vgmsinventorypurchases.infrastructure.persistence.entity.MaterialEntity;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface MaterialR2dbcRepository extends ReactiveCrudRepository<MaterialEntity, String> {
    Flux<MaterialEntity> findByRecordStatus(String recordStatus);

    Flux<MaterialEntity> findByCategoryId(String categoryId);

    Mono<Boolean> existsByMaterialCode(String materialCode);
}
