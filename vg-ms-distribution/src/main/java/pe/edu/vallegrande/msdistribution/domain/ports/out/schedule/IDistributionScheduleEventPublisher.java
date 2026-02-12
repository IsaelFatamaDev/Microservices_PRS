package pe.edu.vallegrande.msdistribution.domain.ports.out.schedule;

import pe.edu.vallegrande.msdistribution.domain.models.DistributionSchedule;
import reactor.core.publisher.Mono;

import java.util.Map;

public interface IDistributionScheduleEventPublisher {
    Mono<Void> publishScheduleCreated(DistributionSchedule schedule, String createdBy);
    Mono<Void> publishScheduleUpdated(DistributionSchedule schedule, Map<String, Object> changedFields, String updatedBy);
    Mono<Void> publishScheduleDeleted(String scheduleId, String deletedBy, String reason);
    Mono<Void> publishScheduleRestored(String scheduleId, String restoredBy);
}
