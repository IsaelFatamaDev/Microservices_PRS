package pe.edu.vallegrande.vgmsinventorypurchases.application.mappers;

import org.springframework.stereotype.Component;
import pe.edu.vallegrande.vgmsinventorypurchases.application.dto.response.InventoryMovementResponse;
import pe.edu.vallegrande.vgmsinventorypurchases.domain.models.InventoryMovement;

@Component
public class InventoryMovementMapper {

    public InventoryMovementResponse toResponse(InventoryMovement domain) {
        return InventoryMovementResponse.builder()
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
}
