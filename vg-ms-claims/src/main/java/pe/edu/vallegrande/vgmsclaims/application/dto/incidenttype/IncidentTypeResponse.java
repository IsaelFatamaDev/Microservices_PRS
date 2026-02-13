package pe.edu.vallegrande.vgmsclaims.application.dto.incidenttype;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

/**
 * Response DTO for incident types.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class IncidentTypeResponse {

    private String id;
    private String organizationId;
    private String typeCode;
    private String typeName;
    private String description;
    private String priorityLevel;
    private Integer estimatedResolutionTime;
    private Boolean requiresExternalService;
    private String recordStatus;
    private Instant createdAt;
    private Instant updatedAt;
}
