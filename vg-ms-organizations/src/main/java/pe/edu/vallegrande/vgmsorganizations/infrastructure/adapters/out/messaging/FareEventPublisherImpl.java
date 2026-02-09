package pe.edu.vallegrande.vgmsorganizations.infrastructure.adapters.out.messaging;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import pe.edu.vallegrande.vgmsorganizations.application.events.fare.FareCreatedEvent;
import pe.edu.vallegrande.vgmsorganizations.application.events.fare.FareDeletedEvent;
import pe.edu.vallegrande.vgmsorganizations.application.events.fare.FareRestoredEvent;
import pe.edu.vallegrande.vgmsorganizations.application.events.fare.FareUpdatedEvent;
import pe.edu.vallegrande.vgmsorganizations.domain.models.Fare;
import pe.edu.vallegrande.vgmsorganizations.domain.ports.out.fare.IFareEventPublisher;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class FareEventPublisherImpl implements IFareEventPublisher {

    private static final String EXCHANGE = "jass.events";
    private final RabbitTemplate rabbitTemplate;

    @Override
    public Mono<Void> publishFareCreated(Fare fare, String createdBy) {
        return Mono.fromRunnable(() -> {
            FareCreatedEvent event = FareCreatedEvent.builder()
                .eventId(UUID.randomUUID().toString())
                .timestamp(LocalDateTime.now())
                .fareId(fare.getId())
                .organizationId(fare.getOrganizationId())
                .fareType(fare.getFareType())
                .amount(fare.getAmount())
                .createdBy(createdBy)
                .correlationId(MDC.get("correlationId"))
                .build();
            rabbitTemplate.convertAndSend(EXCHANGE, "fare.created", event);
        }).subscribeOn(Schedulers.boundedElastic()).then();
    }

    @Override
    public Mono<Void> publishFareUpdated(Fare fare, Map<String, Object> changedFields, String updatedBy) {
        return Mono.fromRunnable(() -> {
            FareUpdatedEvent event = FareUpdatedEvent.builder()
                .eventId(UUID.randomUUID().toString())
                .timestamp(LocalDateTime.now())
                .fareId(fare.getId())
                .organizationId(fare.getOrganizationId())
                .changedFields(changedFields)
                .updatedBy(updatedBy)
                .correlationId(MDC.get("correlationId"))
                .build();
            rabbitTemplate.convertAndSend(EXCHANGE, "fare.updated", event);
        }).subscribeOn(Schedulers.boundedElastic()).then();
    }

    @Override
    public Mono<Void> publishFareDeleted(String fareId, String organizationId, String reason, String deletedBy) {
        return Mono.fromRunnable(() -> {
            FareDeletedEvent event = FareDeletedEvent.builder()
                .eventId(UUID.randomUUID().toString())
                .timestamp(LocalDateTime.now())
                .fareId(fareId)
                .organizationId(organizationId)
                .reason(reason)
                .deletedBy(deletedBy)
                .correlationId(MDC.get("correlationId"))
                .build();
            rabbitTemplate.convertAndSend(EXCHANGE, "fare.deleted", event);
        }).subscribeOn(Schedulers.boundedElastic()).then();
    }

    @Override
    public Mono<Void> publishFareRestored(String fareId, String organizationId, String restoredBy) {
        return Mono.fromRunnable(() -> {
            FareRestoredEvent event = FareRestoredEvent.builder()
                .eventId(UUID.randomUUID().toString())
                .timestamp(LocalDateTime.now())
                .fareId(fareId)
                .organizationId(organizationId)
                .restoredBy(restoredBy)
                .correlationId(MDC.get("correlationId"))
                .build();
            rabbitTemplate.convertAndSend(EXCHANGE, "fare.restored", event);
        }).subscribeOn(Schedulers.boundedElastic()).then();
    }
}