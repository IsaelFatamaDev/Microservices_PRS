package pe.edu.vallegrande.vgmsinfrastructure.infrastructure.messaging;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import pe.edu.vallegrande.vgmsinfrastructure.application.events.external.IncidentCreatedEvent;
import pe.edu.vallegrande.vgmsinfrastructure.infrastructure.config.RabbitMQConfig;

@Slf4j
@Component
@RequiredArgsConstructor
public class IncidentEventListener {

    @RabbitListener(queues = RabbitMQConfig.CLAIMS_QUEUE)
    public void handleIncidentCreated(IncidentCreatedEvent event) {
        log.info("Received incident.created event - Incident: {}, Water box: {}", event.getIncidentId(), event.getWaterBoxId());
        log.info("Incident reported by {} - Description: {}", event.getReportedBy(), event.getDescription());
    }
}
