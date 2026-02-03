package pe.edu.vallegrande.vgmsusers.infrastructure.adapters.out.messaging;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import pe.edu.vallegrande.vgmsusers.application.events.*;
import pe.edu.vallegrande.vgmsusers.domain.models.User;
import pe.edu.vallegrande.vgmsusers.domain.models.valueobjects.RecordStatus;
import pe.edu.vallegrande.vgmsusers.domain.ports.out.IUserEventPublisher;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

import static pe.edu.vallegrande.vgmsusers.infrastructure.config.RabbitMQConfig.*;

@Slf4j
@RequiredArgsConstructor
@Component
public class UserEventPublisherImpl implements IUserEventPublisher {

    private final RabbitTemplate rabbitTemplate;
    private static final String EXCHANGE = "user.events";

    @Override
    public Mono<Void> publishUserCreated(User user, String createdBy) {
        return Mono.fromRunnable(() -> {
            UserCreatedEvent event = UserCreatedEvent.builder()
                .eventId(UUID.randomUUID().toString())
                .timestamp(LocalDateTime.now())
                .userId(user.getId())
                .organizationId(user.getOrganizationId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .documentNumber(user.getDocumentNumber())
                .role(user.getRole().name())
                .createdBy(createdBy)
                .correlationId(MDC.get("correlationId"))
                .build();
            rabbitTemplate.convertAndSend(EXCHANGE, USER_CREATED_KEY, event);
            log.info("User created event published: {} - user: {}", event, user);
        }).subscribeOn(Schedulers.boundedElastic()).then();
    }

    @Override
    public Mono<Void> publishUserUpdated(User user, Map<String, Object> changedFields, String updatedBy) {
        return Mono.fromRunnable(() -> {
                UserUpdatedEvent event = UserUpdatedEvent.builder()
                    .eventId(UUID.randomUUID().toString())
                    .timestamp(LocalDateTime.now())
                    .userId(user.getId())
                    .organizationId(user.getOrganizationId())
                    .changedFields(changedFields)
                    .updatedBy(updatedBy)
                    .correlationId(MDC.get("correlationId"))
                    .build();
                rabbitTemplate.convertAndSend(EXCHANGE, USER_UPDATED_KEY, event);
                log.info("User updated event published: {} - user: {}", event, user);
            }
        ).subscribeOn(Schedulers.boundedElastic()).then();
    }

    @Override
    public Mono<Void> publishUserDeleted(String userId, String organizationId, String deletedBy, String reason) {
        return Mono.fromRunnable(() ->{
            UserDeletedEvent event = UserDeletedEvent.builder()
                .eventId(UUID.randomUUID().toString())
                .timestamp(LocalDateTime.now())
                .userId(userId)
                .organizationId(organizationId)
                .previousStatus(RecordStatus.ACTIVE.name())
                .deletedBy(deletedBy)
                .reason(reason)
                .correlationId(MDC.get("correlationId"))
                .build();

            rabbitTemplate.convertAndSend(EXCHANGE, USER_DELETED_KEY, event);
            log.info("User deleted event published: {} - user: {}", event, userId);
        }).subscribeOn(Schedulers.boundedElastic()).then();
    }

    @Override
    public Mono<Void> publishUserRestored(String userId, String organizationId, String restoredBy) {
        return Mono.fromRunnable(() ->{
            UserRestoredEvent event = UserRestoredEvent.builder()
                .eventId(UUID.randomUUID().toString())
                .timestamp(LocalDateTime.now())
                .userId(userId)
                .organizationId(organizationId)
                .previousStatus(RecordStatus.INACTIVE.name())
                .correlationId(MDC.get("correlationId"))
                .build();

            rabbitTemplate.convertAndSend(EXCHANGE, USER_RESTORED_KEY, event);
            log.info("User restore event published: {} - user: {}", event, userId);
        }).subscribeOn(Schedulers.boundedElastic()).then();
    }

    @Override
    public Mono<Void> publishUserPurged(User user, String purgedBy, String reason) {
        return Mono.fromRunnable(() ->{
            UserPurgedEvent event = UserPurgedEvent.builder()
                .eventId(UUID.randomUUID().toString())
                .timestamp(LocalDateTime.now())
                .userId(user.getId())
                .organizationId(user.getOrganizationId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .purgedBy(purgedBy)
                .reason(reason)
                .correlationId(MDC.get("correlationId"))
                .build();

            rabbitTemplate.convertAndSend(EXCHANGE, USER_PURGED_KEY, event);
            log.info("User purge event published: {} - user: {}", event, user);
        }).subscribeOn(Schedulers.boundedElastic()).then();
    }
}
