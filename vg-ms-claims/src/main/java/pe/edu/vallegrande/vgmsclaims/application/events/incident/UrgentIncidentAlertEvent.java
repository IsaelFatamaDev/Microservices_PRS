package pe.edu.vallegrande.vgmsclaims.application.events.incident;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

/**
 * Alert event for urgent incidents (CRITICAL severity)
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UrgentIncidentAlertEvent {
    
    private String incidentId;
    private String incidentCode;
    private String title;
    private String severity;
    private String zoneId;
    private Integer affectedBoxesCount;
    private Instant alertTime;
}
