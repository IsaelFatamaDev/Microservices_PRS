package pe.edu.vallegrande.vgmsclaims.domain.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pe.edu.vallegrande.vgmsclaims.domain.models.valueobjects.RecordStatus;

import java.time.Instant;

/**
 * Domain entity representing an Incident Type.
 * Does not contain infrastructure annotations.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IncidentType {
    
    private String id;
    private String organizationId;
    private String typeCode;
    private String typeName;
    private String description;
    private String priorityLevel;
    private Integer estimatedResolutionTime; // hours
    private Boolean requiresExternalService;
    
    @Builder.Default
    private RecordStatus recordStatus = RecordStatus.ACTIVE;
    
    private Instant createdAt;
    private Instant updatedAt;

    /**
     * Checks if type is active
     */
    public boolean isActive() {
        return recordStatus == RecordStatus.ACTIVE;
    }
}
