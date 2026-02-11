package pe.edu.vallegrande.vgmsnotifications.infrastructure.adapters.out.messaging;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import pe.edu.vallegrande.vgmsnotifications.application.events.UserCreatedEvent;
import pe.edu.vallegrande.vgmsnotifications.application.events.UserDeletedEvent;
import pe.edu.vallegrande.vgmsnotifications.application.events.UserRestoredEvent;
import pe.edu.vallegrande.vgmsnotifications.application.handlers.UserEventHandler;
import pe.edu.vallegrande.vgmsnotifications.infrastructure.config.RabbitMQConfig;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserEventListener {

    private final UserEventHandler handler;

    @RabbitListener(queues = RabbitMQConfig.QUEUE_USER_CREATED)
    public void onUserCreated(UserCreatedEvent event) {
        log.info("[user.created] Message received");
        handler.handleUserCreated(event)
                .doOnSuccess(n -> log.info("[user.created] Processed: {}", event.getFullName()))
                .doOnError(e -> log.error("[user.created] Error: {}", e.getMessage(), e))
                .subscribe();
    }

    @RabbitListener(queues = RabbitMQConfig.QUEUE_USER_DELETED)
    public void onUserDeleted(UserDeletedEvent event) {
        log.info("[user.deleted] Message received");
        handler.handleUserDeleted(event)
                .doOnSuccess(n -> log.info("[user.deleted] Processed: {}", event.getFullName()))
                .doOnError(e -> log.error("[user.deleted] Error: {}", e.getMessage(), e))
                .subscribe();
    }

    @RabbitListener(queues = RabbitMQConfig.QUEUE_USER_RESTORED)
    public void onUserRestored(UserRestoredEvent event) {
        log.info("[user.restored] Message received");
        handler.handleUserRestored(event)
                .doOnSuccess(n -> log.info("[user.restored] Processed: {}", event.getClientName()))
                .doOnError(e -> log.error("[user.restored] Error: {}", e.getMessage(), e))
                .subscribe();
    }
}
