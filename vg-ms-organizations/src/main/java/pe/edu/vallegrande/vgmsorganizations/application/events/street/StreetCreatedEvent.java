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
public class StreetCreatedEvent {
    @Builder.Default
    private String eventType = "STREET_CREATED";
    private String eventId;
    private LocalDateTime timestamp;
    private String streetId;
    private String zoneId;
    private String organizationId;
    private String streetType;
    private String streetName;
    private String createdBy;
    private String correlationId;
}
