package pe.edu.vallegrande.vgmsinfrastructure.infrastructure.messaging;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import pe.edu.vallegrande.vgmsinfrastructure.application.events.WaterBoxAssignedEvent;
import pe.edu.vallegrande.vgmsinfrastructure.application.events.WaterBoxCreatedEvent;
import pe.edu.vallegrande.vgmsinfrastructure.application.events.WaterBoxDeletedEvent;
import pe.edu.vallegrande.vgmsinfrastructure.application.events.WaterBoxReconnectedEvent;
import pe.edu.vallegrande.vgmsinfrastructure.application.events.WaterBoxRestoredEvent;
import pe.edu.vallegrande.vgmsinfrastructure.application.events.WaterBoxSuspendedEvent;
import pe.edu.vallegrande.vgmsinfrastructure.application.events.WaterBoxTransferredEvent;
import pe.edu.vallegrande.vgmsinfrastructure.application.events.WaterBoxUpdatedEvent;
import pe.edu.vallegrande.vgmsinfrastructure.domain.ports.out.IInfrastructureEventPublisher;
import pe.edu.vallegrande.vgmsinfrastructure.infrastructure.config.RabbitMQConfig;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class InfrastructureEventPublisherImpl implements IInfrastructureEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    @Override
    public Mono<Void> publishWaterBoxCreated(String waterBoxId, String boxCode, String createdBy) {
        return publishEvent("waterbox.created", WaterBoxCreatedEvent.builder()
                .waterBoxId(waterBoxId)
                .boxCode(boxCode)
                .createdBy(createdBy)
                .timestamp(LocalDateTime.now())
                .build());
    }

    @Override
    public Mono<Void> publishWaterBoxUpdated(String waterBoxId, String updatedBy) {
        return publishEvent("waterbox.updated", WaterBoxUpdatedEvent.builder()
                .waterBoxId(waterBoxId)
                .updatedBy(updatedBy)
                .timestamp(LocalDateTime.now())
                .build());
    }

    @Override
    public Mono<Void> publishWaterBoxDeleted(String waterBoxId, String deletedBy) {
        return publishEvent("waterbox.deleted", WaterBoxDeletedEvent.builder()
                .waterBoxId(waterBoxId)
                .deletedBy(deletedBy)
                .timestamp(LocalDateTime.now())
                .build());
    }

    @Override
    public Mono<Void> publishWaterBoxRestored(String waterBoxId, String restoredBy) {
        return publishEvent("waterbox.restored", WaterBoxRestoredEvent.builder()
                .waterBoxId(waterBoxId)
                .restoredBy(restoredBy)
                .timestamp(LocalDateTime.now())
                .build());
    }

    @Override
    public Mono<Void> publishWaterBoxAssigned(String waterBoxId, String userId, String assignedBy) {
        return publishEvent("waterbox.assigned", WaterBoxAssignedEvent.builder()
                .waterBoxId(waterBoxId)
                .userId(userId)
                .assignedBy(assignedBy)
                .timestamp(LocalDateTime.now())
                .build());
    }

    @Override
    public Mono<Void> publishWaterBoxTransferred(String waterBoxId, String fromUserId, String toUserId, String createdBy) {
        return publishEvent("waterbox.transferred", WaterBoxTransferredEvent.builder()
                .waterBoxId(waterBoxId)
                .fromUserId(fromUserId)
                .toUserId(toUserId)
                .createdBy(createdBy)
                .timestamp(LocalDateTime.now())
                .build());
    }

    @Override
    public Mono<Void> publishWaterBoxSuspended(String waterBoxId, String suspendedBy) {
        return publishEvent("waterbox.suspended", WaterBoxSuspendedEvent.builder()
                .waterBoxId(waterBoxId)
                .suspendedBy(suspendedBy)
                .timestamp(LocalDateTime.now())
                .build());
    }

    @Override
    public Mono<Void> publishWaterBoxReconnected(String waterBoxId, String reconnectedBy) {
        return publishEvent("waterbox.reconnected", WaterBoxReconnectedEvent.builder()
                .waterBoxId(waterBoxId)
                .reconnectedBy(reconnectedBy)
                .timestamp(LocalDateTime.now())
                .build());
    }

    private Mono<Void> publishEvent(String routingKey, Object event) {
        return Mono.fromRunnable(() -> {
            try {
                rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE_NAME, routingKey, event);
                log.info("Event published successfully - routing key: {}", routingKey);
            } catch (Exception e) {
                log.error("Failed to publish event with routing key {}: {}", routingKey, e.getMessage());
            }
        }).subscribeOn(Schedulers.boundedElastic()).then();
    }
}
