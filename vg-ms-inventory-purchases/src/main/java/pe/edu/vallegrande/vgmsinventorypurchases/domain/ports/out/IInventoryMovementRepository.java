package pe.edu.vallegrande.vgmsinventorypurchases.domain.ports.out;

import pe.edu.vallegrande.vgmsinventorypurchases.domain.models.InventoryMovement;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface IInventoryMovementRepository {
    Mono<InventoryMovement> save(InventoryMovement movement);

    Mono<InventoryMovement> findById(String id);

    Flux<InventoryMovement> findAll();

    Flux<InventoryMovement> findByMaterialId(String materialId);

    Flux<InventoryMovement> findByMovementType(String movementType);
}
