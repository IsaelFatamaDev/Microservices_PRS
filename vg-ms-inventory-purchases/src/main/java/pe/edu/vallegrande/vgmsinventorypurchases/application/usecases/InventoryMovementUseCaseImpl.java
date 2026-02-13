package pe.edu.vallegrande.vgmsinventorypurchases.application.usecases;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pe.edu.vallegrande.vgmsinventorypurchases.domain.exceptions.InsufficientStockException;
import pe.edu.vallegrande.vgmsinventorypurchases.domain.exceptions.MaterialNotFoundException;
import pe.edu.vallegrande.vgmsinventorypurchases.domain.models.InventoryMovement;
import pe.edu.vallegrande.vgmsinventorypurchases.domain.models.valueobjects.MovementType;
import pe.edu.vallegrande.vgmsinventorypurchases.domain.ports.in.IInventoryMovementUseCase;
import pe.edu.vallegrande.vgmsinventorypurchases.domain.ports.out.IInventoryEventPublisher;
import pe.edu.vallegrande.vgmsinventorypurchases.domain.ports.out.IInventoryMovementRepository;
import pe.edu.vallegrande.vgmsinventorypurchases.domain.ports.out.IMaterialRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class InventoryMovementUseCaseImpl implements IInventoryMovementUseCase {

    private final IInventoryMovementRepository movementRepository;
    private final IMaterialRepository materialRepository;
    private final IInventoryEventPublisher eventPublisher;

    @Override
    public Mono<InventoryMovement> create(InventoryMovement movement, String createdBy) {
        log.info("Creating inventory movement for material: {}", movement.getMaterialId());
        return materialRepository.findById(movement.getMaterialId())
                .switchIfEmpty(Mono.error(new MaterialNotFoundException(movement.getMaterialId())))
                .flatMap(material -> {
                    Double currentStock = material.getCurrentStock() != null ? material.getCurrentStock() : 0.0;

                    // Validate stock for OUT movements
                    if (movement.getMovementType() == MovementType.OUT && currentStock < movement.getQuantity()) {
                        return Mono.error(new InsufficientStockException(
                                material.getId(), movement.getQuantity(), currentStock));
                    }

                    Double newStock;
                    if (movement.getMovementType() == MovementType.IN) {
                        newStock = currentStock + movement.getQuantity();
                    } else if (movement.getMovementType() == MovementType.OUT) {
                        newStock = currentStock - movement.getQuantity();
                    } else {
                        // ADJUSTMENT - use the quantity as the new absolute stock
                        newStock = movement.getQuantity();
                    }

                    InventoryMovement newMovement = movement.toBuilder()
                            .previousStock(currentStock)
                            .newStock(newStock)
                            .createdBy(createdBy)
                            .build();

                    return movementRepository.save(newMovement)
                            .flatMap(saved -> materialRepository.save(material.adjustStock(newStock, createdBy))
                                    .flatMap(updatedMaterial -> {
                                        Mono<Void> stockEvent = eventPublisher.publishStockUpdated(
                                                material.getId(), currentStock, newStock, createdBy);
                                        if (updatedMaterial.isLowStock()) {
                                            return stockEvent.then(eventPublisher.publishLowStockAlert(
                                                    material.getId(), material.getMaterialCode(),
                                                    updatedMaterial.getCurrentStock(), updatedMaterial.getMinStock()))
                                                    .thenReturn(saved);
                                        }
                                        return stockEvent.thenReturn(saved);
                                    }));
                })
                .doOnSuccess(saved -> log.info("Inventory movement created successfully: {}", saved.getId()))
                .doOnError(error -> log.error("Error creating inventory movement: {}", error.getMessage()));
    }

    @Override
    public Mono<InventoryMovement> findById(String id) {
        log.debug("Finding inventory movement by ID: {}", id);
        return movementRepository.findById(id);
    }

    @Override
    public Flux<InventoryMovement> findAll() {
        log.debug("Finding all inventory movements");
        return movementRepository.findAll();
    }

    @Override
    public Flux<InventoryMovement> findByMaterialId(String materialId) {
        log.debug("Finding inventory movements by material: {}", materialId);
        return movementRepository.findByMaterialId(materialId);
    }
}
