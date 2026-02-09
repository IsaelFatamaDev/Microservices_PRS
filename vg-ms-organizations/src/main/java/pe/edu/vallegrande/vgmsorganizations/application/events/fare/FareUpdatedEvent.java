package pe.edu.vallegrande.vgmsorganizations.application.events.fare;

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
public class FareUpdatedEvent {
    @Builder.Default
    private String eventType = "FARE_UPDATED";
    private String eventId;
    private LocalDateTime timestamp;
    private String fareId;
    private String organizationId;
    private Map<String, Object> changedFields;
    private String updatedBy;
    private String correlationId;
}