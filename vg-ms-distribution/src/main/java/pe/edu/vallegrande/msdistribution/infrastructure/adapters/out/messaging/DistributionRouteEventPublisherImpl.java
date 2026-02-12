package pe.edu.vallegrande.msdistribution.infrastructure.adapters.out.messaging;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import pe.edu.vallegrande.msdistribution.application.events.route.*;
import pe.edu.vallegrande.msdistribution.domain.models.DistributionRoute;
import pe.edu.vallegrande.msdistribution.domain.ports.out.route.IDistributionRouteEventPublisher;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

/**
 * Implementación del puerto IDistributionRouteEventPublisher.
 * Publica eventos de DistributionRoute a RabbitMQ.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DistributionRouteEventPublisherImpl implements IDistributionRouteEventPublisher {

    private static final String EXCHANGE = "jass.events";
    private static final String ROUTING_KEY_CREATED = "distribution.route.created";
    private static final String ROUTING_KEY_UPDATED = "distribution.route.updated";
    private static final String ROUTING_KEY_DELETED = "distribution.route.deleted";
    private static final String ROUTING_KEY_RESTORED = "distribution.route.restored";

    private final RabbitTemplate rabbitTemplate;

    @Override
    public Mono<Void> publishRouteCreated(DistributionRoute route, String createdBy) {
        return Mono.fromRunnable(() -> {
            DistributionRouteCreatedEvent event = DistributionRouteCreatedEvent.builder()
                    .eventId(UUID.randomUUID().toString())
                    .timestamp(LocalDateTime.now())
                    .correlationId(UUID.randomUUID().toString())
                    .routeId(route.getId())
                    .organizationId(route.getOrganizationId())
                    .routeName(route.getRouteName())
                    .createdBy(createdBy)
                    .build();

            rabbitTemplate.convertAndSend(EXCHANGE, ROUTING_KEY_CREATED, event);
            log.info("Published distribution.route.created event for route: {}", route.getId());
        });
    }

    @Override
    public Mono<Void> publishRouteUpdated(DistributionRoute route, Map<String, Object> changedFields, String updatedBy) {
        return Mono.fromRunnable(() -> {
            DistributionRouteUpdatedEvent event = DistributionRouteUpdatedEvent.builder()
                    .eventId(UUID.randomUUID().toString())
                    .timestamp(LocalDateTime.now())
                    .correlationId(UUID.randomUUID().toString())
                    .routeId(route.getId())
                    .organizationId(route.getOrganizationId())
                    .changedFields(changedFields)
                    .updatedBy(updatedBy)
                    .build();

            rabbitTemplate.convertAndSend(EXCHANGE, ROUTING_KEY_UPDATED, event);
            log.info("Published distribution.route.updated event for route: {}", route.getId());
        });
    }

    @Override
    public Mono<Void> publishRouteDeleted(String routeId, String deletedBy, String reason) {
        return Mono.fromRunnable(() -> {
            DistributionRouteDeletedEvent event = DistributionRouteDeletedEvent.builder()
                    .eventId(UUID.randomUUID().toString())
                    .timestamp(LocalDateTime.now())
                    .correlationId(UUID.randomUUID().toString())
                    .routeId(routeId)
                    .reason(reason)
                    .deletedBy(deletedBy)
                    .build();

            rabbitTemplate.convertAndSend(EXCHANGE, ROUTING_KEY_DELETED, event);
            log.info("Published distribution.route.deleted event for route: {}", routeId);
        });
    }

    @Override
    public Mono<Void> publishRouteRestored(String routeId, String restoredBy) {
        return Mono.fromRunnable(() -> {
            DistributionRouteRestoredEvent event = DistributionRouteRestoredEvent.builder()
                    .eventId(UUID.randomUUID().toString())
                    .timestamp(LocalDateTime.now())
                    .correlationId(UUID.randomUUID().toString())
                    .routeId(routeId)
                    .restoredBy(restoredBy)
                    .build();

            rabbitTemplate.convertAndSend(EXCHANGE, ROUTING_KEY_RESTORED, event);
            log.info("Published distribution.route.restored event for route: {}", routeId);
        });
    }
}
