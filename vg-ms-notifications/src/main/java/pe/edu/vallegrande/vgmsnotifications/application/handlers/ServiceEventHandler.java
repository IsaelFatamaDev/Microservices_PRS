package pe.edu.vallegrande.vgmsnotifications.application.handlers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import pe.edu.vallegrande.vgmsnotifications.application.events.ServiceCutExecutedEvent;
import pe.edu.vallegrande.vgmsnotifications.application.events.ServiceCutWarningEvent;
import pe.edu.vallegrande.vgmsnotifications.application.events.ServiceRestoredEvent;
import pe.edu.vallegrande.vgmsnotifications.domain.models.valueobjects.NotificationType;
import pe.edu.vallegrande.vgmsnotifications.domain.ports.in.ISendNotificationUseCase;
import reactor.core.publisher.Mono;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class ServiceEventHandler {

    private final ISendNotificationUseCase sendNotificationUseCase;

    public Mono<Void> handleServiceCutWarning(ServiceCutWarningEvent event) {
        log.info("Processing service.cut.warning for: {}", event.getClientName());
        Map<String, String> variables = Map.of(
                "name", event.getClientName(),
                "cutDate", event.getCutDate(),
                "debtAmount", event.getDebtAmount());
        return sendNotificationUseCase.send(
                event.getPhoneNumber(), event.getClientName(),
                NotificationType.SERVICE_CUT_WARNING, variables,
                "vg-ms-distribution", event.getEventId(), event.getUserId()).then();
    }

    public Mono<Void> handleServiceCutExecuted(ServiceCutExecutedEvent event) {
        log.info("Processing service.cut.executed for: {}", event.getClientName());
        Map<String, String> variables = Map.of(
                "name", event.getClientName(),
                "date", event.getCutDate(),
                "debtAmount", event.getDebtAmount());
        return sendNotificationUseCase.send(
                event.getPhoneNumber(), event.getClientName(),
                NotificationType.SERVICE_CUT_EXECUTED, variables,
                "vg-ms-distribution", event.getEventId(), event.getUserId()).then();
    }

    public Mono<Void> handleServiceRestored(ServiceRestoredEvent event) {
        log.info("Processing service.restored for: {}", event.getClientName());
        Map<String, String> variables = Map.of(
                "name", event.getClientName(),
                "date", event.getRestoredDate(),
                "amountPaid", event.getAmountPaid());
        return sendNotificationUseCase.send(
                event.getPhoneNumber(), event.getClientName(),
                NotificationType.SERVICE_RESTORED, variables,
                "vg-ms-distribution", event.getEventId(), event.getUserId()).then();
    }
}
