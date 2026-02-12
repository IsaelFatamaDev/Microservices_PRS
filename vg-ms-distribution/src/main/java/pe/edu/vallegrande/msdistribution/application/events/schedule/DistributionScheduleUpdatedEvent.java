package pe.edu.vallegrande.msdistribution.application.events.schedule;

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
public class DistributionScheduleUpdatedEvent {
    
    @Builder.Default
    private String eventType = "DISTRIBUTION_SCHEDULE_UPDATED";
    
    private String eventId;
    private LocalDateTime timestamp;
    private String correlationId;
    
    private String scheduleId;
    private String organizationId;
    private Map<String, Object> changedFields;
    
    private String updatedBy;
}
