package pe.edu.vallegrande.vgmsclaims.application.events.incident;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

/**
 * Event emitted when an incident is created
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IncidentCreatedEvent {
    
    private String incidentId;
    private String incidentCode;
    private String title;
    private String severity;
    private String zoneId;
    private String reportedByUserId;
    private Instant createdAt;
}
