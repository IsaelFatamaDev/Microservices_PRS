package pe.edu.vallegrande.vgmsorganizations.application.events.street;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StreetRestoredEvent {
    @Builder.Default
    private String eventType = "STREET_RESTORED";
    private String eventId;
    private LocalDateTime timestamp;
    private String streetId;
    private String zoneId;
    private String restoredBy;
    private String correlationId;
}
