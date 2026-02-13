package pe.edu.vallegrande.vgmsclaims.infrastructure.persistence.documents;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;

/**
 * MongoDB document for incident types
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "incident_types")
public class IncidentTypeDocument {
    
    @Id
    private String id;
    
    @Field("organization_id")
    private String organizationId;
    
    @Field("type_code")
    private String typeCode;
    
    @Field("type_name")
    private String typeName;
    
    private String description;
    
    @Field("priority_level")
    private String priorityLevel;
    
    @Field("estimated_resolution_time")
    private Integer estimatedResolutionTime;
    
    @Field("requires_external_service")
    private Boolean requiresExternalService;
    
    private String status;
    
    @Field("record_status")
    private String recordStatus;
    
    @Field("created_at")
    private Instant createdAt;
    
    @Field("updated_at")
    private Instant updatedAt;
    
    public void prePersist() {
        if (this.createdAt == null) {
            this.createdAt = Instant.now();
        }
        this.updatedAt = Instant.now();
    }
}
