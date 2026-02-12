package pe.edu.vallegrande.msdistribution.application.events.schedule;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DistributionScheduleCreatedEvent {

    @Builder.Default
    private String eventType = "DISTRIBUTION_SCHEDULE_CREATED";

    private String eventId;
    private LocalDateTime timestamp;
    private String correlationId;

    private String scheduleId;
    private String organizationId;
    private String zoneId;
    private List<String> daysOfWeek;

    private String createdBy;
}
