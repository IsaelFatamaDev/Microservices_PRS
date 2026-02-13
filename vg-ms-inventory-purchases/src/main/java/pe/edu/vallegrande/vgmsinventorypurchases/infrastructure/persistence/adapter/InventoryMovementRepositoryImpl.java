package pe.edu.vallegrande.vgmsinventorypurchases.infrastructure.persistence.adapter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import pe.edu.vallegrande.vgmsinventorypurchases.domain.models.InventoryMovement;
import pe.edu.vallegrande.vgmsinventorypurchases.domain.models.valueobjects.MovementType;
import pe.edu.vallegrande.vgmsinventorypurchases.domain.ports.out.IInventoryMovementRepository;
import pe.edu.vallegrande.vgmsinventorypurchases.infrastructure.persistence.entity.InventoryMovementEntity;
import pe.edu.vallegrande.vgmsinventorypurchases.infrastructure.persistence.repository.InventoryMovementR2dbcRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class InventoryMovementRepositoryImpl implements IInventoryMovementRepository {

    private final InventoryMovementR2dbcRepository r2dbcRepository;

    @Override
    public Mono<InventoryMovement> save(InventoryMovement movement) {
        InventoryMovementEntity entity = toEntity(movement);
        if (entity.getId() == null) {
            entity.setId(UUID.randomUUID().toString());
        }
        return r2dbcRepository.save(entity).map(this::toDomain);
    }

    @Override
    public Mono<InventoryMovement> findById(String id) {
        return r2dbcRepository.findById(id).map(this::toDomain);
    }

    @Override
    public Flux<InventoryMovement> findAll() {
        return r2dbcRepository.findAll().map(this::toDomain);
    }

    @Override
    public Flux<InventoryMovement> findByMaterialId(String materialId) {
        return r2dbcRepository.findByMaterialId(materialId).map(this::toDomain);
    }

    @Override
    public Flux<InventoryMovement> findByMovementType(String movementType) {
        return r2dbcRepository.findByMovementType(movementType).map(this::toDomain);
    }

    private InventoryMovementEntity toEntity(InventoryMovement domain) {
        return InventoryMovementEntity.builder()
                .id(domain.getId())
                .organizationId(domain.getOrganizationId())
                .materialId(domain.getMaterialId())
                .movementType(domain.getMovementType() != null ? domain.getMovementType().name() : null)
                .quantity(domain.getQuantity())
                .unitPrice(domain.getUnitPrice())
                .previousStock(domain.getPreviousStock())
                .newStock(domain.getNewStock())
                .referenceId(domain.getReferenceId())
                .referenceType(domain.getReferenceType())
                .notes(domain.getNotes())
                .createdAt(domain.getCreatedAt())
                .createdBy(domain.getCreatedBy())
                .build();
    }

    private InventoryMovement toDomain(InventoryMovementEntity entity) {
        return InventoryMovement.builder()
                .id(entity.getId())
                .organizationId(entity.getOrganizationId())
                .materialId(entity.getMaterialId())
                .movementType(entity.getMovementType() != null ? MovementType.valueOf(entity.getMovementType()) : null)
                .quantity(entity.getQuantity())
                .unitPrice(entity.getUnitPrice())
                .previousStock(entity.getPreviousStock())
                .newStock(entity.getNewStock())
                .referenceId(entity.getReferenceId())
                .referenceType(entity.getReferenceType())
                .notes(entity.getNotes())
                .createdAt(entity.getCreatedAt())
                .createdBy(entity.getCreatedBy())
                .build();
    }
}
