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
 * MongoDB document for incidents
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "incidents")
public class IncidentDocument {
    
    @Id
    private String id;
    
    @Field("organization_id")
    private String organizationId;
    
    @Field("incident_code")
    private String incidentCode;
    
    @Field("incident_type_id")
    private String incidentTypeId;
    
    @Field("incident_category")
    private String incidentCategory;
    
    @Field("zone_id")
    private String zoneId;
    
    @Field("incident_date")
    private Instant incidentDate;
    
    private String title;
    
    private String description;
    
    private String severity;
    
    private String status;
    
    @Field("affected_boxes_count")
    private Integer affectedBoxesCount;
    
    @Field("reported_by_user_id")
    private String reportedByUserId;
    
    @Field("assigned_to_user_id")
    private String assignedToUserId;
    
    @Field("resolved_by_user_id")
    private String resolvedByUserId;
    
    private Boolean resolved;
    
    @Field("resolution_notes")
    private String resolutionNotes;
    
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
