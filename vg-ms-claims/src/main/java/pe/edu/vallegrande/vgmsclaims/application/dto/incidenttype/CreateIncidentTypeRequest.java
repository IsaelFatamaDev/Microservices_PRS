package pe.edu.vallegrande.vgmsclaims.application.dto.incidenttype;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO to create a new incident type.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateIncidentTypeRequest {

    @NotBlank(message = "Type code is required")
    private String typeCode;

    @NotBlank(message = "Type name is required")
    private String typeName;

    private String description;

    private String priorityLevel;

    private Integer estimatedResolutionTime;

    private Boolean requiresExternalService;

    private String organizationId;
}
