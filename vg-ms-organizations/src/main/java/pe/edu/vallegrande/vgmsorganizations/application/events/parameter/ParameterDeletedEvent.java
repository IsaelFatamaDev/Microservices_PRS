package pe.edu.vallegrande.vgmsorganizations.application.events.parameter;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ParameterDeletedEvent {
    @Builder.Default
    private String eventType = "PARAMETER_DELETED";
    private String eventId;
    private LocalDateTime timestamp;
    private String parameterId;
    private String organizationId;
    private String reason;
    private String deletedBy;
    private String correlationId;
}