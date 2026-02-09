package pe.edu.vallegrande.vgmsorganizations.infrastructure.adapters.out.messaging;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import pe.edu.vallegrande.vgmsorganizations.application.events.organization.OrganizationCreatedEvent;
import pe.edu.vallegrande.vgmsorganizations.application.events.organization.OrganizationDeletedEvent;
import pe.edu.vallegrande.vgmsorganizations.application.events.organization.OrganizationRestoredEvent;
import pe.edu.vallegrande.vgmsorganizations.application.events.organization.OrganizationUpdatedEvent;
import pe.edu.vallegrande.vgmsorganizations.domain.models.Organization;
import pe.edu.vallegrande.vgmsorganizations.domain.ports.out.organization.IOrganizationEventPublisher;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrganizationEventPublisherImpl implements IOrganizationEventPublisher {

    private static final String EXCHANGE = "jass.events";
    private final RabbitTemplate rabbitTemplate;

    @Override
    public Mono<Void> publishOrganizationCreated(Organization org, String createdBy) {
        return Mono.fromRunnable(() -> {
            OrganizationCreatedEvent event = OrganizationCreatedEvent.builder()
                .eventId(UUID.randomUUID().toString())
                .timestamp(LocalDateTime.now())
                .organizationId(org.getId())
                .organizationName(org.getOrganizationName())
                .district(org.getDistrict())
                .province(org.getProvince())
                .department(org.getDepartment())
                .createdBy(createdBy)
                .correlationId(MDC.get("correlationId"))
                .build();
            rabbitTemplate.convertAndSend(EXCHANGE, "organization.created", event);
            log.info("Event published: organization.created - {}", org.getId());
        }).subscribeOn(Schedulers.boundedElastic()).then();
    }

    @Override
    public Mono<Void> publishOrganizationUpdated(Organization org, Map<String, Object> changedFields, String updatedBy) {
        return Mono.fromRunnable(() -> {
            OrganizationUpdatedEvent event = OrganizationUpdatedEvent.builder()
                .eventId(UUID.randomUUID().toString())
                .timestamp(LocalDateTime.now())
                .organizationId(org.getId())
                .changedFields(changedFields)
                .updatedBy(updatedBy)
                .correlationId(MDC.get("correlationId"))
                .build();
            rabbitTemplate.convertAndSend(EXCHANGE, "organization.updated", event);
            log.info("Event published: organization.updated - {}", org.getId());
        }).subscribeOn(Schedulers.boundedElastic()).then();
    }

    @Override
    public Mono<Void> publishOrganizationDeleted(String organizationId, String reason, String deletedBy) {
        return Mono.fromRunnable(() -> {
            OrganizationDeletedEvent event = OrganizationDeletedEvent.builder()
                .eventId(UUID.randomUUID().toString())
                .timestamp(LocalDateTime.now())
                .organizationId(organizationId)
                .previousStatus("ACTIVE")
                .reason(reason)
                .deletedBy(deletedBy)
                .correlationId(MDC.get("correlationId"))
                .build();
            rabbitTemplate.convertAndSend(EXCHANGE, "organization.deleted", event);
            log.info("Event published: organization.deleted - {}", organizationId);
        }).subscribeOn(Schedulers.boundedElastic()).then();
    }

    @Override
    public Mono<Void> publishOrganizationRestored(String organizationId, String restoredBy) {
        return Mono.fromRunnable(() -> {
            OrganizationRestoredEvent event = OrganizationRestoredEvent.builder()
                .eventId(UUID.randomUUID().toString())
                .timestamp(LocalDateTime.now())
                .organizationId(organizationId)
                .previousStatus("INACTIVE")
                .restoredBy(restoredBy)
                .correlationId(MDC.get("correlationId"))
                .build();
            rabbitTemplate.convertAndSend(EXCHANGE, "organization.restored", event);
            log.info("Event published: organization.restored - {}", organizationId);
        }).subscribeOn(Schedulers.boundedElastic()).then();
    }
}
