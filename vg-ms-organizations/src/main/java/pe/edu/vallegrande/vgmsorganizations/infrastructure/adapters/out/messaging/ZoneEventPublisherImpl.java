package pe.edu.vallegrande.vgmsorganizations.infrastructure.adapters.out.messaging;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import pe.edu.vallegrande.vgmsorganizations.application.events.zone.ZoneCreatedEvent;
import pe.edu.vallegrande.vgmsorganizations.application.events.zone.ZoneDeletedEvent;
import pe.edu.vallegrande.vgmsorganizations.application.events.zone.ZoneRestoredEvent;
import pe.edu.vallegrande.vgmsorganizations.application.events.zone.ZoneUpdatedEvent;
import pe.edu.vallegrande.vgmsorganizations.domain.models.Zone;
import pe.edu.vallegrande.vgmsorganizations.domain.ports.out.zone.IZoneEventPublisher;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class ZoneEventPublisherImpl implements IZoneEventPublisher {

    private static final String EXCHANGE = "jass.events";
    private final RabbitTemplate rabbitTemplate;

    @Override
    public Mono<Void> publishZoneCreated(Zone zone, String createdBy) {
        return Mono.fromRunnable(() -> {
            ZoneCreatedEvent event = ZoneCreatedEvent.builder()
                .eventId(UUID.randomUUID().toString())
                .timestamp(LocalDateTime.now())
                .zoneId(zone.getId())
                .organizationId(zone.getOrganizationId())
                .zoneName(zone.getZoneName())
                .createdBy(createdBy)
                .correlationId(MDC.get("correlationId"))
                .build();
            rabbitTemplate.convertAndSend(EXCHANGE, "zone.created", event);
            log.info("Event published: zone.created - {}", zone.getId());
        }).subscribeOn(Schedulers.boundedElastic()).then();
    }

    @Override
    public Mono<Void> publishZoneUpdated(Zone zone, Map<String, Object> changedFields, String updatedBy) {
        return Mono.fromRunnable(() -> {
            ZoneUpdatedEvent event = ZoneUpdatedEvent.builder()
                .eventId(UUID.randomUUID().toString())
                .timestamp(LocalDateTime.now())
                .zoneId(zone.getId())
                .organizationId(zone.getOrganizationId())
                .changedFields(changedFields)
                .updatedBy(updatedBy)
                .correlationId(MDC.get("correlationId"))
                .build();
            rabbitTemplate.convertAndSend(EXCHANGE, "zone.updated", event);
        }).subscribeOn(Schedulers.boundedElastic()).then();
    }

    @Override
    public Mono<Void> publishZoneDeleted(String zoneId, String organizationId, String reason, String deletedBy) {
        return Mono.fromRunnable(() -> {
            ZoneDeletedEvent event = ZoneDeletedEvent.builder()
                .eventId(UUID.randomUUID().toString())
                .timestamp(LocalDateTime.now())
                .zoneId(zoneId)
                .organizationId(organizationId)
                .reason(reason)
                .deletedBy(deletedBy)
                .correlationId(MDC.get("correlationId"))
                .build();
            rabbitTemplate.convertAndSend(EXCHANGE, "zone.deleted", event);
        }).subscribeOn(Schedulers.boundedElastic()).then();
    }

    @Override
    public Mono<Void> publishZoneRestored(String zoneId, String organizationId, String restoredBy) {
        return Mono.fromRunnable(() -> {
            ZoneRestoredEvent event = ZoneRestoredEvent.builder()
                .eventId(UUID.randomUUID().toString())
                .timestamp(LocalDateTime.now())
                .zoneId(zoneId)
                .organizationId(organizationId)
                .restoredBy(restoredBy)
                .correlationId(MDC.get("correlationId"))
                .build();
            rabbitTemplate.convertAndSend(EXCHANGE, "zone.restored", event);
        }).subscribeOn(Schedulers.boundedElastic()).then();
    }
}
