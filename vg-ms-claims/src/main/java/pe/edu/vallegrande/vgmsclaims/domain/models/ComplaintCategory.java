package pe.edu.vallegrande.vgmsclaims.domain.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pe.edu.vallegrande.vgmsclaims.domain.models.valueobjects.RecordStatus;

import java.time.Instant;

/**
 * Domain entity representing a Complaint Category.
 * Does not contain infrastructure annotations.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ComplaintCategory {
    
    private String id;
    private String organizationId;
    private String categoryCode;
    private String categoryName;
    private String description;
    private String priorityLevel;
    private Integer maxResponseTime; // hours
    
    @Builder.Default
    private RecordStatus recordStatus = RecordStatus.ACTIVE;
    
    private Instant createdAt;
    private Instant updatedAt;

    /**
     * Checks if category is active
     */
    public boolean isActive() {
        return recordStatus == RecordStatus.ACTIVE;
    }
}
