package pe.edu.vallegrande.vgmsinventorypurchases.domain.models;

import lombok.Builder;
import lombok.Getter;
import pe.edu.vallegrande.vgmsinventorypurchases.domain.models.valueobjects.MovementType;

import java.time.LocalDateTime;

@Getter
@Builder(toBuilder = true)
public class InventoryMovement {
    private String id;
    private String organizationId;
    private LocalDateTime createdAt;
    private String createdBy;
    private String materialId;
    private MovementType movementType;
    private Double quantity;
    private Double unitPrice;
    private Double previousStock;
    private Double newStock;
    private String referenceId;
    private String referenceType;
    private String notes;

    public static InventoryMovement createEntry(String organizationId, String materialId,
            Double quantity, Double unitPrice,
            Double previousStock, String referenceId,
            String referenceType, String createdBy) {
        return InventoryMovement.builder()
                .organizationId(organizationId)
                .materialId(materialId)
                .movementType(MovementType.IN)
                .quantity(quantity)
                .unitPrice(unitPrice)
                .previousStock(previousStock)
                .newStock(previousStock + quantity)
                .referenceId(referenceId)
                .referenceType(referenceType)
                .createdAt(LocalDateTime.now())
                .createdBy(createdBy)
                .build();
    }

    public static InventoryMovement createExit(String organizationId, String materialId,
            Double quantity, Double unitPrice,
            Double previousStock, String referenceId,
            String referenceType, String createdBy) {
        return InventoryMovement.builder()
                .organizationId(organizationId)
                .materialId(materialId)
                .movementType(MovementType.OUT)
                .quantity(quantity)
                .unitPrice(unitPrice)
                .previousStock(previousStock)
                .newStock(previousStock - quantity)
                .referenceId(referenceId)
                .referenceType(referenceType)
                .createdAt(LocalDateTime.now())
                .createdBy(createdBy)
                .build();
    }
}
