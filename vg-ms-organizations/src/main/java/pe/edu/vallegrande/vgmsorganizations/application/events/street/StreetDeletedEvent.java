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
public class StreetDeletedEvent {
    @Builder.Default
    private String eventType = "STREET_DELETED";
    private String eventId;
    private LocalDateTime timestamp;
    private String streetId;
    private String zoneId;
    private String reason;
    private String deletedBy;
    private String correlationId;
}