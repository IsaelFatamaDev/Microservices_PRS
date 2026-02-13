package pe.edu.vallegrande.vgmsinventorypurchases.domain.ports.in;

import pe.edu.vallegrande.vgmsinventorypurchases.domain.models.InventoryMovement;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface IInventoryMovementUseCase {
    Mono<InventoryMovement> create(InventoryMovement movement, String createdBy);

    Mono<InventoryMovement> findById(String id);

    Flux<InventoryMovement> findAll();

    Flux<InventoryMovement> findByMaterialId(String materialId);
}
