package pe.edu.vallegrande.vgmsorganizations.application.events.zone;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ZoneCreatedEvent {
    @Builder.Default
    private String eventType = "ZONE_CREATED";
    private String eventId;
    private LocalDateTime timestamp;
    private String zoneId;
    private String organizationId;
    private String zoneName;
    private String createdBy;
    private String correlationId;
}
