package pe.edu.vallegrande.msdistribution.application.events.schedule;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DistributionScheduleRestoredEvent {
    
    @Builder.Default
    private String eventType = "DISTRIBUTION_SCHEDULE_RESTORED";
    
    private String eventId;
    private LocalDateTime timestamp;
    private String correlationId;
    
    private String scheduleId;
    private String organizationId;
    
    private String restoredBy;
}
