package pe.edu.vallegrande.vgmsorganizations.application.events.fare;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FareCreatedEvent {
    @Builder.Default
    private String eventType = "FARE_CREATED";
    private String eventId;
    private LocalDateTime timestamp;
    private String fareId;
    private String organizationId;
    private String fareType;
    private Double amount;
    private String createdBy;
    private String correlationId;
}
