package pe.edu.vallegrande.vgmsnotifications.infrastructure.adapters.out.messaging;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import pe.edu.vallegrande.vgmsnotifications.application.events.ServiceCutExecutedEvent;
import pe.edu.vallegrande.vgmsnotifications.application.events.ServiceCutWarningEvent;
import pe.edu.vallegrande.vgmsnotifications.application.events.ServiceRestoredEvent;
import pe.edu.vallegrande.vgmsnotifications.application.handlers.ServiceEventHandler;
import pe.edu.vallegrande.vgmsnotifications.infrastructure.config.RabbitMQConfig;

@Slf4j
@Component
@RequiredArgsConstructor
public class ServiceEventListener {

    private final ServiceEventHandler handler;

    @RabbitListener(queues = RabbitMQConfig.QUEUE_SERVICE_CUT_WARNING)
    public void onServiceCutWarning(ServiceCutWarningEvent event) {
        log.info("[service.cut.warning] Message received");
        handler.handleServiceCutWarning(event)
                .doOnSuccess(n -> log.info("[service.cut.warning] Processed: {}", event.getClientName()))
                .doOnError(e -> log.error("[service.cut.warning] Error: {}", e.getMessage(), e))
                .subscribe();
    }

    @RabbitListener(queues = RabbitMQConfig.QUEUE_SERVICE_CUT_EXECUTED)
    public void onServiceCutExecuted(ServiceCutExecutedEvent event) {
        log.info("[service.cut.executed] Message received");
        handler.handleServiceCutExecuted(event)
                .doOnSuccess(n -> log.info("[service.cut.executed] Processed: {}", event.getClientName()))
                .doOnError(e -> log.error("[service.cut.executed] Error: {}", e.getMessage(), e))
                .subscribe();
    }

    @RabbitListener(queues = RabbitMQConfig.QUEUE_SERVICE_RESTORED)
    public void onServiceRestored(ServiceRestoredEvent event) {
        log.info("[service.restored] Message received");
        handler.handleServiceRestored(event)
                .doOnSuccess(n -> log.info("[service.restored] Processed: {}", event.getClientName()))
                .doOnError(e -> log.error("[service.restored] Error: {}", e.getMessage(), e))
                .subscribe();
    }
}
