package pe.edu.vallegrande.vgmsclaims.application.dto.incident;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

/**
 * Response DTO for incidents
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class IncidentResponse {
    
    private String id;
    private String organizationId;
    private String incidentCode;
    private String incidentTypeId;
    private String incidentCategory;
    private String zoneId;
    private Instant incidentDate;
    private String title;
    private String description;
    private String severity;
    private String status;
    private Integer affectedBoxesCount;
    private String reportedByUserId;
    private String assignedToUserId;
    private String resolvedByUserId;
    private Boolean resolved;
    private String resolutionNotes;
    private String recordStatus;
    private Instant createdAt;
    private Instant updatedAt;
}
