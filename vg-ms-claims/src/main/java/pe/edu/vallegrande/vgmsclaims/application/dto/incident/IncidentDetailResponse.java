package pe.edu.vallegrande.vgmsclaims.application.dto.incident;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

/**
 * Detailed response DTO for incidents (includes resolution)
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class IncidentDetailResponse {
    
    private String id;
    private String organizationId;
    private String incidentCode;
    private String incidentTypeId;
    private String incidentTypeName;
    private String incidentCategory;
    private String zoneId;
    private String zoneName;
    private Instant incidentDate;
    private String title;
    private String description;
    private String severity;
    private String status;
    private Integer affectedBoxesCount;
    private String reportedByUserId;
    private String reportedByUserName;
    private String assignedToUserId;
    private String assignedToUserName;
    private String resolvedByUserId;
    private String resolvedByUserName;
    private Boolean resolved;
    private String resolutionNotes;
    private String recordStatus;
    private Instant createdAt;
    private Instant updatedAt;
    
    private ResolutionDetail resolution;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ResolutionDetail {
        private String id;
        private Instant resolutionDate;
        private String resolutionType;
        private String actionsTaken;
        private List<MaterialUsedDetail> materialsUsed;
        private Integer laborHours;
        private BigDecimal totalCost;
        private Boolean qualityCheck;
        private Boolean followUpRequired;
        private String resolutionNotes;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MaterialUsedDetail {
        private String productId;
        private String productName;
        private Integer quantity;
        private String unit;
        private BigDecimal unitCost;
        private BigDecimal totalCost;
    }
}
