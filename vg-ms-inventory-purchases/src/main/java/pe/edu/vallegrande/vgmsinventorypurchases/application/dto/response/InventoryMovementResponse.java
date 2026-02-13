package pe.edu.vallegrande.vgmsinventorypurchases.application.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class InventoryMovementResponse {
    private final String id;
    private final String organizationId;
    private final String materialId;
    private final String movementType;
    private final Double quantity;
    private final Double unitPrice;
    private final Double previousStock;
    private final Double newStock;
    private final String referenceId;
    private final String referenceType;
    private final String notes;
    private final LocalDateTime createdAt;
    private final String createdBy;
}
