package pe.edu.vallegrande.vgmsclaims.application.dto.incident;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO to assign an incident to a technician
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AssignIncidentRequest {
    
    @NotBlank(message = "User ID is required")
    private String userId;
    
    private String notes;
}
