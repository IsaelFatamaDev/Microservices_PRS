package pe.edu.vallegrande.vgmsorganizations.application.events.street;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StreetUpdatedEvent {
    @Builder.Default
    private String eventType = "STREET_UPDATED";
    private String eventId;
    private LocalDateTime timestamp;
    private String streetId;
    private String zoneId;
    private Map<String, Object> changedFields;
    private String updatedBy;
    private String correlationId;
}
