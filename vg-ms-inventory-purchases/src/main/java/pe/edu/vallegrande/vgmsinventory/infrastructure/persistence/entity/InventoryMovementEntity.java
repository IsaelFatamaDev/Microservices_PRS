package pe.edu.vallegrande.vgmsinventorypurchases.infrastructure.persistence.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Table("inventory_movements")
public class InventoryMovementEntity {
    @Id
    private String id;
    @Column("organization_id")
    private String organizationId;
    @Column("created_at")
    private LocalDateTime createdAt;
    @Column("created_by")
    private String createdBy;
    @Column("material_id")
    private String materialId;
    @Column("movement_type")
    private String movementType;
    @Column("quantity")
    private Double quantity;
    @Column("unit_price")
    private Double unitPrice;
    @Column("previous_stock")
    private Double previousStock;
    @Column("new_stock")
    private Double newStock;
    @Column("reference_id")
    private String referenceId;
    @Column("reference_type")
    private String referenceType;
    @Column("notes")
    private String notes;
}
