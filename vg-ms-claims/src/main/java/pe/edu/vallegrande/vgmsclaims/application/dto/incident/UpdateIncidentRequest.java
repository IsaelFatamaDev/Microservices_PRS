package pe.edu.vallegrande.vgmsclaims.application.dto.incident;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO to update an existing incident
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateIncidentRequest {
    
    private String title;
    private String description;
    private String severity;
    private String status;
    private Integer affectedBoxesCount;
    private String resolutionNotes;
}
