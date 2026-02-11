package pe.edu.vallegrande.vgmsnotifications.application.handlers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import pe.edu.vallegrande.vgmsnotifications.application.events.PaymentCompletedEvent;
import pe.edu.vallegrande.vgmsnotifications.application.events.PaymentOverdueEvent;
import pe.edu.vallegrande.vgmsnotifications.application.events.ReceiptGeneratedEvent;
import pe.edu.vallegrande.vgmsnotifications.domain.models.valueobjects.NotificationType;
import pe.edu.vallegrande.vgmsnotifications.domain.ports.in.ISendNotificationUseCase;
import reactor.core.publisher.Mono;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentEventHandler {

    private final ISendNotificationUseCase sendNotificationUseCase;

    public Mono<Void> handlePaymentCompleted(PaymentCompletedEvent event) {
        log.info("Processing payment.completed for: {}", event.getClientName());
        Map<String, String> variables = Map.of(
                "name", event.getClientName(),
                "amount", event.getAmount(),
                "date", event.getPaymentDate(),
                "receiptCode", event.getReceiptCode(),
                "period", event.getPeriod());
        return sendNotificationUseCase.send(
                event.getPhoneNumber(), event.getClientName(),
                NotificationType.PAYMENT_CONFIRMATION, variables,
                "vg-ms-payments-billing", event.getEventId(), event.getUserId()).then();
    }

    public Mono<Void> handlePaymentOverdue(PaymentOverdueEvent event) {
        log.info("Processing payment.overdue for: {}", event.getClientName());
        Map<String, String> variables = Map.of(
                "name", event.getClientName(),
                "amount", event.getAmount(),
                "period", event.getPeriod(),
                "monthsOverdue", event.getMonthsOverdue());
        return sendNotificationUseCase.send(
                event.getPhoneNumber(), event.getClientName(),
                NotificationType.OVERDUE_REMINDER, variables,
                "vg-ms-payments-billing", event.getEventId(), event.getUserId()).then();
    }

    public Mono<Void> handleReceiptGenerated(ReceiptGeneratedEvent event) {
        log.info("Processing receipt.generated for: {}", event.getClientName());
        Map<String, String> variables = Map.of(
                "name", event.getClientName(),
                "receiptCode", event.getReceiptCode(),
                "period", event.getPeriod(),
                "totalAmount", event.getTotalAmount(),
                "dueDate", event.getDueDate());
        return sendNotificationUseCase.send(
                event.getPhoneNumber(), event.getClientName(),
                NotificationType.RECEIPT_NOTIFICATION, variables,
                "vg-ms-payments-billing", event.getEventId(), event.getUserId()).then();
    }
}
