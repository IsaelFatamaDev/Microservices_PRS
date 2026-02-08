package pe.edu.vallegrande.vgmsauthentication.infrastructure.messaging.listeners;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import pe.edu.vallegrande.vgmsauthentication.application.events.external.*;
import pe.edu.vallegrande.vgmsauthentication.application.events.handlers.UserEventHandler;
import pe.edu.vallegrande.vgmsauthentication.infrastructure.config.RabbitMQConfig;
import reactor.core.scheduler.Schedulers;

/**
 * Listener de eventos de usuario provenientes de vg-ms-users via RabbitMQ.
 * Procesa eventos del ciclo de vida de usuarios para sincronizar con Keycloak.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class UserEventListener {

    private final UserEventHandler userEventHandler;

    @RabbitListener(queues = RabbitMQConfig.USER_CREATED_QUEUE)
    public void handleUserCreated(UserCreatedEvent event) {
        log.info("Received user.created event for userId: {}", event.getUserId());

        userEventHandler.handleUserCreated(event)
                .subscribeOn(Schedulers.boundedElastic())
                .subscribe(
                        v -> log.info("User created event processed successfully: {}", event.getUserId()),
                        error -> log.error("Error processing user created event for {}: {}",
                                event.getUserId(), error.getMessage()));
    }

    @RabbitListener(queues = RabbitMQConfig.USER_UPDATED_QUEUE)
    public void handleUserUpdated(UserUpdatedEvent event) {
        log.info("Received user.updated event for userId: {}", event.getUserId());

        userEventHandler.handleUserUpdated(event)
                .subscribeOn(Schedulers.boundedElastic())
                .subscribe(
                        v -> log.info("User updated event processed successfully: {}", event.getUserId()),
                        error -> log.error("Error processing user updated event for {}: {}",
                                event.getUserId(), error.getMessage()));
    }

    @RabbitListener(queues = RabbitMQConfig.USER_DELETED_QUEUE)
    public void handleUserDeleted(UserDeletedEvent event) {
        log.info("Received user.deleted event for userId: {}", event.getUserId());

        userEventHandler.handleUserDeleted(event)
                .subscribeOn(Schedulers.boundedElastic())
                .subscribe(
                        v -> log.info("User deleted event processed successfully: {}", event.getUserId()),
                        error -> log.error("Error processing user deleted event for {}: {}",
                                event.getUserId(), error.getMessage()));
    }

    @RabbitListener(queues = RabbitMQConfig.USER_RESTORED_QUEUE)
    public void handleUserRestored(UserRestoredEvent event) {
        log.info("Received user.restored event for userId: {}", event.getUserId());

        userEventHandler.handleUserRestored(event)
                .subscribeOn(Schedulers.boundedElastic())
                .subscribe(
                        v -> log.info("User restored event processed successfully: {}", event.getUserId()),
                        error -> log.error("Error processing user restored event for {}: {}",
                                event.getUserId(), error.getMessage()));
    }

    @RabbitListener(queues = RabbitMQConfig.USER_PURGED_QUEUE)
    public void handleUserPurged(UserPurgedEvent event) {
        log.info("Received user.purged event for userId: {}", event.getUserId());

        userEventHandler.handleUserPurged(event)
                .subscribeOn(Schedulers.boundedElastic())
                .subscribe(
                        v -> log.info("User purged event processed successfully: {}", event.getUserId()),
                        error -> log.error("Error processing user purged event for {}: {}",
                                event.getUserId(), error.getMessage()));
    }
}
