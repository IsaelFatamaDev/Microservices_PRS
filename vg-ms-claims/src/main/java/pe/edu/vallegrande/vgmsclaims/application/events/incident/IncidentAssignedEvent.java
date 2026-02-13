package pe.edu.vallegrande.vgmsclaims.application.events.incident;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

/**
 * Event emitted when an incident is assigned
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IncidentAssignedEvent {
    
    private String incidentId;
    private String incidentCode;
    private String assignedToUserId;
    private String severity;
    private Instant assignedAt;
}
