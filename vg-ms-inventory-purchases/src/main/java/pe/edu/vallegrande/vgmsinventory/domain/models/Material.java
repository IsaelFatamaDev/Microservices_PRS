package pe.edu.vallegrande.vgmsinventorypurchases.domain.models;

import lombok.Builder;
import lombok.Getter;
import pe.edu.vallegrande.vgmsinventorypurchases.domain.models.valueobjects.RecordStatus;
import pe.edu.vallegrande.vgmsinventorypurchases.domain.models.valueobjects.Unit;

import java.time.LocalDateTime;

@Getter
@Builder(toBuilder = true)
public class Material {
    private String id;
    private String organizationId;
    private RecordStatus recordStatus;
    private LocalDateTime createdAt;
    private String createdBy;
    private LocalDateTime updatedAt;
    private String updatedBy;
    private String materialCode;
    private String materialName;
    private String categoryId;
    private Unit unit;
    private Double currentStock;
    private Double minStock;
    private Double unitPrice;

    public boolean isActiveRecord() {
        return recordStatus == RecordStatus.ACTIVE;
    }

    public boolean isLowStock() {
        return currentStock != null && minStock != null && currentStock <= minStock;
    }

    public Material markAsDeleted(String deletedBy) {
        return this.toBuilder()
                .recordStatus(RecordStatus.INACTIVE)
                .updatedAt(LocalDateTime.now())
                .updatedBy(deletedBy)
                .build();
    }

    public Material restore(String restoredBy) {
        return this.toBuilder()
                .recordStatus(RecordStatus.ACTIVE)
                .updatedAt(LocalDateTime.now())
                .updatedBy(restoredBy)
                .build();
    }

    public Material adjustStock(Double newStock, String updatedBy) {
        return this.toBuilder()
                .currentStock(newStock)
                .updatedAt(LocalDateTime.now())
                .updatedBy(updatedBy)
                .build();
    }

    public Material updateWith(Material changes, String updatedBy) {
        var builder = this.toBuilder()
                .updatedAt(LocalDateTime.now())
                .updatedBy(updatedBy);
        if (changes.getMaterialCode() != null)
            builder.materialCode(changes.getMaterialCode());
        if (changes.getMaterialName() != null)
            builder.materialName(changes.getMaterialName());
        if (changes.getCategoryId() != null)
            builder.categoryId(changes.getCategoryId());
        if (changes.getUnit() != null)
            builder.unit(changes.getUnit());
        if (changes.getMinStock() != null)
            builder.minStock(changes.getMinStock());
        if (changes.getUnitPrice() != null)
            builder.unitPrice(changes.getUnitPrice());
        return builder.build();
    }
}
