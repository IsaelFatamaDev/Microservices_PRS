package pe.edu.vallegrande.vgmsinventorypurchases.infrastructure.persistence.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Table("materials")
public class MaterialEntity implements Persistable<String> {
    @Id
    private String id;
    @Column("organization_id")
    private String organizationId;
    @Column("record_status")
    private String recordStatus;
    @Column("created_at")
    private LocalDateTime createdAt;
    @Column("created_by")
    private String createdBy;
    @Column("updated_at")
    private LocalDateTime updatedAt;
    @Column("updated_by")
    private String updatedBy;
    @Column("material_code")
    private String materialCode;
    @Column("material_name")
    private String materialName;
    @Column("category_id")
    private String categoryId;
    @Column("unit")
    private String unit;
    @Column("current_stock")
    private Double currentStock;
    @Column("min_stock")
    private Double minStock;
    @Column("unit_price")
    private Double unitPrice;

    @Transient
    @Builder.Default
    private boolean isNew = true;

    @Override
    public boolean isNew() {
        return isNew || id == null;
    }
}
