package pe.edu.vallegrande.vgmsinventorypurchases.domain.models;

import lombok.Builder;
import lombok.Getter;
import pe.edu.vallegrande.vgmsinventorypurchases.domain.models.valueobjects.RecordStatus;

import java.time.LocalDateTime;

@Getter
@Builder(toBuilder = true)
public class ProductCategory {
    private String id;
    private String organizationId;
    private RecordStatus recordStatus;
    private LocalDateTime createdAt;
    private String createdBy;
    private LocalDateTime updatedAt;
    private String updatedBy;
    private String categoryName;
    private String description;

    public boolean isActiveRecord() {
        return recordStatus == RecordStatus.ACTIVE;
    }

    public ProductCategory markAsDeleted(String deletedBy) {
        return this.toBuilder()
                .recordStatus(RecordStatus.INACTIVE)
                .updatedAt(LocalDateTime.now())
                .updatedBy(deletedBy)
                .build();
    }

    public ProductCategory restore(String restoredBy) {
        return this.toBuilder()
                .recordStatus(RecordStatus.ACTIVE)
                .updatedAt(LocalDateTime.now())
                .updatedBy(restoredBy)
                .build();
    }

    public ProductCategory updateWith(ProductCategory changes, String updatedBy) {
        var builder = this.toBuilder()
                .updatedAt(LocalDateTime.now())
                .updatedBy(updatedBy);
        if (changes.getCategoryName() != null)
            builder.categoryName(changes.getCategoryName());
        if (changes.getDescription() != null)
            builder.description(changes.getDescription());
        return builder.build();
    }
}
