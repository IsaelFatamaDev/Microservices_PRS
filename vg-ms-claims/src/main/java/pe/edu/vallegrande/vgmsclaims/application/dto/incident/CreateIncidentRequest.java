package pe.edu.vallegrande.vgmsclaims.application.dto.incident;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

/**
 * DTO to create a new incident
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateIncidentRequest {
    
    private String incidentCode;
    
    @NotBlank(message = "Title is required")
    private String title;
    
    @NotBlank(message = "Description is required")
    private String description;
    
    @NotNull(message = "Incident type is required")
    private String incidentTypeId;
    
    private String incidentCategory;
    
    @NotNull(message = "Zone is required")
    private String zoneId;
    
    private Instant incidentDate;
    
    @NotNull(message = "Severity is required")
    private String severity;
    
    private String status;
    
    private Integer affectedBoxesCount;
    
    private String organizationId;
    
    private String reportedByUserId;
    
    private String assignedToUserId;
    
    private String resolvedByUserId;
    
    private Boolean resolved;
    
    private String resolutionNotes;
    
    private String recordStatus;
}
