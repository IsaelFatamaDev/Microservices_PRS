package pe.edu.vallegrande.vgmsorganizations.infrastructure.adapters.out.messaging;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import pe.edu.vallegrande.vgmsorganizations.application.events.street.StreetCreatedEvent;
import pe.edu.vallegrande.vgmsorganizations.application.events.street.StreetDeletedEvent;
import pe.edu.vallegrande.vgmsorganizations.application.events.street.StreetRestoredEvent;
import pe.edu.vallegrande.vgmsorganizations.application.events.street.StreetUpdatedEvent;
import pe.edu.vallegrande.vgmsorganizations.domain.models.Street;
import pe.edu.vallegrande.vgmsorganizations.domain.ports.out.street.IStreetEventPublisher;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class StreetEventPublisherImpl implements IStreetEventPublisher {

    private static final String EXCHANGE = "jass.events";
    private final RabbitTemplate rabbitTemplate;

    @Override
    public Mono<Void> publishStreetCreated(Street street, String createdBy) {
        return Mono.fromRunnable(() -> {
            StreetCreatedEvent event = StreetCreatedEvent.builder()
                .eventId(UUID.randomUUID().toString())
                .timestamp(LocalDateTime.now())
                .streetId(street.getId())
                .zoneId(street.getZoneId())
                .organizationId(street.getOrganizationId())
                .streetType(street.getStreetType().name())
                .streetName(street.getStreetName())
                .createdBy(createdBy)
                .correlationId(MDC.get("correlationId"))
                .build();
            rabbitTemplate.convertAndSend(EXCHANGE, "street.created", event);
        }).subscribeOn(Schedulers.boundedElastic()).then();
    }

    @Override
    public Mono<Void> publishStreetUpdated(Street street, Map<String, Object> changedFields, String updatedBy) {
        return Mono.fromRunnable(() -> {
            StreetUpdatedEvent event = StreetUpdatedEvent.builder()
                .eventId(UUID.randomUUID().toString())
                .timestamp(LocalDateTime.now())
                .streetId(street.getId())
                .zoneId(street.getZoneId())
                .changedFields(changedFields)
                .updatedBy(updatedBy)
                .correlationId(MDC.get("correlationId"))
                .build();
            rabbitTemplate.convertAndSend(EXCHANGE, "street.updated", event);
        }).subscribeOn(Schedulers.boundedElastic()).then();
    }

    @Override
    public Mono<Void> publishStreetDeleted(String streetId, String zoneId, String reason, String deletedBy) {
        return Mono.fromRunnable(() -> {
            StreetDeletedEvent event = StreetDeletedEvent.builder()
                .eventId(UUID.randomUUID().toString())
                .timestamp(LocalDateTime.now())
                .streetId(streetId)
                .zoneId(zoneId)
                .reason(reason)
                .deletedBy(deletedBy)
                .correlationId(MDC.get("correlationId"))
                .build();
            rabbitTemplate.convertAndSend(EXCHANGE, "street.deleted", event);
        }).subscribeOn(Schedulers.boundedElastic()).then();
    }

    @Override
    public Mono<Void> publishStreetRestored(String streetId, String zoneId, String restoredBy) {
        return Mono.fromRunnable(() -> {
            StreetRestoredEvent event = StreetRestoredEvent.builder()
                .eventId(UUID.randomUUID().toString())
                .timestamp(LocalDateTime.now())
                .streetId(streetId)
                .zoneId(zoneId)
                .restoredBy(restoredBy)
                .correlationId(MDC.get("correlationId"))
                .build();
            rabbitTemplate.convertAndSend(EXCHANGE, "street.restored", event);
        }).subscribeOn(Schedulers.boundedElastic()).then();
    }
}