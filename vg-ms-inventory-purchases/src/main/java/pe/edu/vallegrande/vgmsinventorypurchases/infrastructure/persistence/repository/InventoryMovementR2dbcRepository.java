package pe.edu.vallegrande.vgmsinventorypurchases.infrastructure.persistence.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import pe.edu.vallegrande.vgmsinventorypurchases.infrastructure.persistence.entity.InventoryMovementEntity;
import reactor.core.publisher.Flux;

@Repository
public interface InventoryMovementR2dbcRepository extends ReactiveCrudRepository<InventoryMovementEntity, String> {
    Flux<InventoryMovementEntity> findByMaterialId(String materialId);

    Flux<InventoryMovementEntity> findByMovementType(String movementType);
}
