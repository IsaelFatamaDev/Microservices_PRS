package pe.edu.vallegrande.vgmsorganizations.application.events.parameter;

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
public class ParameterUpdatedEvent {
    @Builder.Default
    private String eventType = "PARAMETER_UPDATED";
    private String eventId;
    private LocalDateTime timestamp;
    private String parameterId;
    private String organizationId;
    private Map<String, Object> changedFields;
    private String updatedBy;
    private String correlationId;
}
