package pe.edu.vallegrande.vgmsclaims.infrastructure.adapters.out.messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import pe.edu.vallegrande.vgmsclaims.domain.ports.out.IClaimsEventPublisher;
import pe.edu.vallegrande.vgmsclaims.infrastructure.config.RabbitMQConfig;

/**
 * Event publisher implementation using RabbitMQ.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ClaimsEventPublisherImpl implements IClaimsEventPublisher {

    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;

    @Override
    public void publishComplaintCreated(Object event) {
        publishEvent("complaint.created", event);
    }

    @Override
    public void publishComplaintUpdated(Object event) {
        publishEvent("complaint.updated", event);
    }

    @Override
    public void publishComplaintResponseAdded(Object event) {
        publishEvent("complaint.response.added", event);
    }

    @Override
    public void publishComplaintClosed(Object event) {
        publishEvent("complaint.closed", event);
    }

    @Override
    public void publishIncidentCreated(Object event) {
        publishEvent("incident.created", event);
    }

    @Override
    public void publishIncidentAssigned(Object event) {
        publishEvent("incident.assigned", event);
    }

    @Override
    public void publishIncidentUpdated(Object event) {
        publishEvent("incident.updated", event);
    }

    @Override
    public void publishIncidentResolved(Object event) {
        publishEvent("incident.resolved", event);
    }

    @Override
    public void publishIncidentClosed(Object event) {
        publishEvent("incident.closed", event);
    }

    @Override
    public void publishUrgentIncidentAlert(Object event) {
        publishEvent("incident.urgent.alert", event);
    }

    private void publishEvent(String routingKey, Object event) {
        try {
            String message = objectMapper.writeValueAsString(event);
            rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE, routingKey, message);
            log.info("Event published: {} -> {}", routingKey, event.getClass().getSimpleName());
        } catch (Exception e) {
            log.error("Error publishing event {}: {}", routingKey, e.getMessage());
        }
    }
}
