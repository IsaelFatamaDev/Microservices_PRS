package pe.edu.vallegrande.vgmsclaims.infrastructure.persistence.documents;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

/**
 * MongoDB document for incident resolutions
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "incident_resolutions")
public class IncidentResolutionDocument {
    
    @Id
    private String id;
    
    @Field("incident_id")
    private String incidentId;
    
    @Field("resolution_date")
    private Instant resolutionDate;
    
    @Field("resolution_type")
    private String resolutionType;
    
    @Field("actions_taken")
    private String actionsTaken;
    
    @Field("materials_used")
    private List<MaterialUsedEmbedded> materialsUsed;
    
    @Field("labor_hours")
    private Integer laborHours;
    
    @Field("total_cost")
    private BigDecimal totalCost;
    
    @Field("resolved_by_user_id")
    private String resolvedByUserId;
    
    @Field("quality_check")
    private Boolean qualityCheck;
    
    @Field("follow_up_required")
    private Boolean followUpRequired;
    
    @Field("resolution_notes")
    private String resolutionNotes;
    
    @Field("created_at")
    private Instant createdAt;
    
    @Field("updated_at")
    private Instant updatedAt;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MaterialUsedEmbedded {
        @Field("product_id")
        private String productId;
        
        private Integer quantity;
        
        private String unit;
        
        @Field("unit_cost")
        private BigDecimal unitCost;
    }
    
    public void prePersist() {
        if (this.createdAt == null) {
            this.createdAt = Instant.now();
        }
        this.updatedAt = Instant.now();
    }
}
