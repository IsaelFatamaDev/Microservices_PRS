package pe.edu.vallegrande.vgmsinfrastructure.infrastructure.messaging;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import pe.edu.vallegrande.vgmsinfrastructure.application.events.external.ServiceCutScheduledEvent;
import pe.edu.vallegrande.vgmsinfrastructure.application.events.external.ServiceReconnectedEvent;
import pe.edu.vallegrande.vgmsinfrastructure.domain.ports.in.IReconnectWaterBoxUseCase;
import pe.edu.vallegrande.vgmsinfrastructure.domain.ports.in.ISuspendWaterBoxUseCase;
import pe.edu.vallegrande.vgmsinfrastructure.infrastructure.config.RabbitMQConfig;

@Slf4j
@Component
@RequiredArgsConstructor
public class ServiceCutEventListener {

    private final ISuspendWaterBoxUseCase suspendUseCase;
    private final IReconnectWaterBoxUseCase reconnectUseCase;

    @RabbitListener(queues = RabbitMQConfig.COMMERCIAL_QUEUE)
    public void handleServiceCutScheduled(ServiceCutScheduledEvent event) {
        log.info("Received service-cut.scheduled event for water box: {}", event.getWaterBoxId());
        suspendUseCase.execute(event.getWaterBoxId(), event.getScheduledBy())
                .doOnSuccess(v -> log.info("Water box {} suspended due to service cut", event.getWaterBoxId()))
                .doOnError(e -> log.error("Error processing service cut for water box {}: {}", event.getWaterBoxId(), e.getMessage()))
                .subscribe();
    }

    @RabbitListener(queues = RabbitMQConfig.COMMERCIAL_QUEUE)
    public void handleServiceReconnected(ServiceReconnectedEvent event) {
        log.info("Received service-reconnected event for water box: {}", event.getWaterBoxId());
        reconnectUseCase.execute(event.getWaterBoxId(), event.getReconnectedBy())
                .doOnSuccess(v -> log.info("Water box {} reconnected after service restoration", event.getWaterBoxId()))
                .doOnError(e -> log.error("Error processing reconnection for water box {}: {}", event.getWaterBoxId(), e.getMessage()))
                .subscribe();
    }
}
