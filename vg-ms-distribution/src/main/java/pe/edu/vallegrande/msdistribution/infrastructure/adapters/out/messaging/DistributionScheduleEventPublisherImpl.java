package pe.edu.vallegrande.msdistribution.infrastructure.adapters.out.messaging;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import pe.edu.vallegrande.msdistribution.application.events.schedule.*;
import pe.edu.vallegrande.msdistribution.domain.models.DistributionSchedule;
import pe.edu.vallegrande.msdistribution.domain.ports.out.schedule.IDistributionScheduleEventPublisher;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

/**
 * Implementación del puerto IDistributionScheduleEventPublisher.
 * Publica eventos de DistributionSchedule a RabbitMQ.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DistributionScheduleEventPublisherImpl implements IDistributionScheduleEventPublisher {

    private static final String EXCHANGE = "jass.events";
    private static final String ROUTING_KEY_CREATED = "distribution.schedule.created";
    private static final String ROUTING_KEY_UPDATED = "distribution.schedule.updated";
    private static final String ROUTING_KEY_DELETED = "distribution.schedule.deleted";
    private static final String ROUTING_KEY_RESTORED = "distribution.schedule.restored";

    private final RabbitTemplate rabbitTemplate;

    @Override
    public Mono<Void> publishScheduleCreated(DistributionSchedule schedule, String createdBy) {
        return Mono.fromRunnable(() -> {
            DistributionScheduleCreatedEvent event = DistributionScheduleCreatedEvent.builder()
                    .eventId(UUID.randomUUID().toString())
                    .timestamp(LocalDateTime.now())
                    .correlationId(UUID.randomUUID().toString())
                    .scheduleId(schedule.getId())
                    .organizationId(schedule.getOrganizationId())
                    .zoneId(schedule.getZoneId())
                    .daysOfWeek(schedule.getDaysOfWeek())
                    .createdBy(createdBy)
                    .build();

            rabbitTemplate.convertAndSend(EXCHANGE, ROUTING_KEY_CREATED, event);
            log.info("Published distribution.schedule.created event for schedule: {}", schedule.getId());
        });
    }

    @Override
    public Mono<Void> publishScheduleUpdated(DistributionSchedule schedule, Map<String, Object> changedFields, String updatedBy) {
        return Mono.fromRunnable(() -> {
            DistributionScheduleUpdatedEvent event = DistributionScheduleUpdatedEvent.builder()
                    .eventId(UUID.randomUUID().toString())
                    .timestamp(LocalDateTime.now())
                    .correlationId(UUID.randomUUID().toString())
                    .scheduleId(schedule.getId())
                    .organizationId(schedule.getOrganizationId())
                    .changedFields(changedFields)
                    .updatedBy(updatedBy)
                    .build();

            rabbitTemplate.convertAndSend(EXCHANGE, ROUTING_KEY_UPDATED, event);
            log.info("Published distribution.schedule.updated event for schedule: {}", schedule.getId());
        });
    }

    @Override
    public Mono<Void> publishScheduleDeleted(String scheduleId, String deletedBy, String reason) {
        return Mono.fromRunnable(() -> {
            DistributionScheduleDeletedEvent event = DistributionScheduleDeletedEvent.builder()
                    .eventId(UUID.randomUUID().toString())
                    .timestamp(LocalDateTime.now())
                    .correlationId(UUID.randomUUID().toString())
                    .scheduleId(scheduleId)
                    .reason(reason)
                    .deletedBy(deletedBy)
                    .build();

            rabbitTemplate.convertAndSend(EXCHANGE, ROUTING_KEY_DELETED, event);
            log.info("Published distribution.schedule.deleted event for schedule: {}", scheduleId);
        });
    }

    @Override
    public Mono<Void> publishScheduleRestored(String scheduleId, String restoredBy) {
        return Mono.fromRunnable(() -> {
            DistributionScheduleRestoredEvent event = DistributionScheduleRestoredEvent.builder()
                    .eventId(UUID.randomUUID().toString())
                    .timestamp(LocalDateTime.now())
                    .correlationId(UUID.randomUUID().toString())
                    .scheduleId(scheduleId)
                    .restoredBy(restoredBy)
                    .build();

            rabbitTemplate.convertAndSend(EXCHANGE, ROUTING_KEY_RESTORED, event);
            log.info("Published distribution.schedule.restored event for schedule: {}", scheduleId);
        });
    }
}
