package pe.edu.vallegrande.msdistribution.infrastructure.adapters.out.messaging;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import pe.edu.vallegrande.msdistribution.application.events.program.*;
import pe.edu.vallegrande.msdistribution.domain.models.DistributionProgram;
import pe.edu.vallegrande.msdistribution.domain.ports.out.program.IDistributionProgramEventPublisher;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

/**
 * Implementación del puerto IDistributionProgramEventPublisher.
 * Publica eventos de DistributionProgram a RabbitMQ.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DistributionProgramEventPublisherImpl implements IDistributionProgramEventPublisher {

    private static final String EXCHANGE = "jass.events";
    private static final String ROUTING_KEY_CREATED = "distribution.program.created";
    private static final String ROUTING_KEY_UPDATED = "distribution.program.updated";
    private static final String ROUTING_KEY_DELETED = "distribution.program.deleted";
    private static final String ROUTING_KEY_RESTORED = "distribution.program.restored";

    private final RabbitTemplate rabbitTemplate;

    @Override
    public Mono<Void> publishProgramCreated(DistributionProgram program, String createdBy) {
        return Mono.fromRunnable(() -> {
            DistributionProgramCreatedEvent event = DistributionProgramCreatedEvent.builder()
                    .eventId(UUID.randomUUID().toString())
                    .timestamp(LocalDateTime.now())
                    .correlationId(UUID.randomUUID().toString())
                    .programId(program.getId())
                    .organizationId(program.getOrganizationId())
                    .createdBy(createdBy)
                    .build();

            rabbitTemplate.convertAndSend(EXCHANGE, ROUTING_KEY_CREATED, event);
            log.info("Published distribution.program.created event for program: {}", program.getId());
        });
    }

    @Override
    public Mono<Void> publishProgramUpdated(DistributionProgram program, Map<String, Object> changedFields, String updatedBy) {
        return Mono.fromRunnable(() -> {
            DistributionProgramUpdatedEvent event = DistributionProgramUpdatedEvent.builder()
                    .eventId(UUID.randomUUID().toString())
                    .timestamp(LocalDateTime.now())
                    .correlationId(UUID.randomUUID().toString())
                    .programId(program.getId())
                    .organizationId(program.getOrganizationId())
                    .changedFields(changedFields)
                    .updatedBy(updatedBy)
                    .build();

            rabbitTemplate.convertAndSend(EXCHANGE, ROUTING_KEY_UPDATED, event);
            log.info("Published distribution.program.updated event for program: {}", program.getId());
        });
    }

    @Override
    public Mono<Void> publishProgramDeleted(String programId, String deletedBy, String reason) {
        return Mono.fromRunnable(() -> {
            DistributionProgramDeletedEvent event = DistributionProgramDeletedEvent.builder()
                    .eventId(UUID.randomUUID().toString())
                    .timestamp(LocalDateTime.now())
                    .correlationId(UUID.randomUUID().toString())
                    .programId(programId)
                    .reason(reason)
                    .deletedBy(deletedBy)
                    .build();

            rabbitTemplate.convertAndSend(EXCHANGE, ROUTING_KEY_DELETED, event);
            log.info("Published distribution.program.deleted event for program: {}", programId);
        });
    }

    @Override
    public Mono<Void> publishProgramRestored(String programId, String restoredBy) {
        return Mono.fromRunnable(() -> {
            DistributionProgramRestoredEvent event = DistributionProgramRestoredEvent.builder()
                    .eventId(UUID.randomUUID().toString())
                    .timestamp(LocalDateTime.now())
                    .correlationId(UUID.randomUUID().toString())
                    .programId(programId)
                    .restoredBy(restoredBy)
                    .build();

            rabbitTemplate.convertAndSend(EXCHANGE, ROUTING_KEY_RESTORED, event);
            log.info("Published distribution.program.restored event for program: {}", programId);
        });
    }
}
