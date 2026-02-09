package pe.edu.vallegrande.vgmsorganizations.infrastructure.adapters.out.messaging;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import pe.edu.vallegrande.vgmsorganizations.application.events.parameter.ParameterCreatedEvent;
import pe.edu.vallegrande.vgmsorganizations.application.events.parameter.ParameterDeletedEvent;
import pe.edu.vallegrande.vgmsorganizations.application.events.parameter.ParameterRestoredEvent;
import pe.edu.vallegrande.vgmsorganizations.application.events.parameter.ParameterUpdatedEvent;
import pe.edu.vallegrande.vgmsorganizations.domain.models.Parameter;
import pe.edu.vallegrande.vgmsorganizations.domain.ports.out.parameter.IParameterEventPublisher;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class ParameterEventPublisherImpl implements IParameterEventPublisher {

    private static final String EXCHANGE = "jass.events";
    private final RabbitTemplate rabbitTemplate;

    @Override
    public Mono<Void> publishParameterCreated(Parameter param, String createdBy) {
        return Mono.fromRunnable(() -> {
            ParameterCreatedEvent event = ParameterCreatedEvent.builder()
                .eventId(UUID.randomUUID().toString())
                .timestamp(LocalDateTime.now())
                .parameterId(param.getId())
                .organizationId(param.getOrganizationId())
                .parameterType(param.getParameterType())
                .parameterValue(param.getParameterValue())
                .createdBy(createdBy)
                .correlationId(MDC.get("correlationId"))
                .build();
            rabbitTemplate.convertAndSend(EXCHANGE, "parameter.created", event);
        }).subscribeOn(Schedulers.boundedElastic()).then();
    }

    @Override
    public Mono<Void> publishParameterUpdated(Parameter param, Map<String, Object> changedFields, String updatedBy) {
        return Mono.fromRunnable(() -> {
            ParameterUpdatedEvent event = ParameterUpdatedEvent.builder()
                .eventId(UUID.randomUUID().toString())
                .timestamp(LocalDateTime.now())
                .parameterId(param.getId())
                .organizationId(param.getOrganizationId())
                .changedFields(changedFields)
                .updatedBy(updatedBy)
                .correlationId(MDC.get("correlationId"))
                .build();
            rabbitTemplate.convertAndSend(EXCHANGE, "parameter.updated", event);
        }).subscribeOn(Schedulers.boundedElastic()).then();
    }

    @Override
    public Mono<Void> publishParameterDeleted(String parameterId, String organizationId, String reason, String deletedBy) {
        return Mono.fromRunnable(() -> {
            ParameterDeletedEvent event = ParameterDeletedEvent.builder()
                .eventId(UUID.randomUUID().toString())
                .timestamp(LocalDateTime.now())
                .parameterId(parameterId)
                .organizationId(organizationId)
                .reason(reason)
                .deletedBy(deletedBy)
                .correlationId(MDC.get("correlationId"))
                .build();
            rabbitTemplate.convertAndSend(EXCHANGE, "parameter.deleted", event);
        }).subscribeOn(Schedulers.boundedElastic()).then();
    }

    @Override
    public Mono<Void> publishParameterRestored(String parameterId, String organizationId, String restoredBy) {
        return Mono.fromRunnable(() -> {
            ParameterRestoredEvent event = ParameterRestoredEvent.builder()
                .eventId(UUID.randomUUID().toString())
                .timestamp(LocalDateTime.now())
                .parameterId(parameterId)
                .organizationId(organizationId)
                .restoredBy(restoredBy)
                .correlationId(MDC.get("correlationId"))
                .build();
            rabbitTemplate.convertAndSend(EXCHANGE, "parameter.restored", event);
        }).subscribeOn(Schedulers.boundedElastic()).then();
    }
}