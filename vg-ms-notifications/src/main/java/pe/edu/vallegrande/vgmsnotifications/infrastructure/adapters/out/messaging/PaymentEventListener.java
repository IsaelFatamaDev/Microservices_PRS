package pe.edu.vallegrande.vgmsnotifications.infrastructure.adapters.out.messaging;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import pe.edu.vallegrande.vgmsnotifications.application.events.PaymentCompletedEvent;
import pe.edu.vallegrande.vgmsnotifications.application.events.PaymentOverdueEvent;
import pe.edu.vallegrande.vgmsnotifications.application.events.ReceiptGeneratedEvent;
import pe.edu.vallegrande.vgmsnotifications.application.handlers.PaymentEventHandler;
import pe.edu.vallegrande.vgmsnotifications.infrastructure.config.RabbitMQConfig;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentEventListener {

    private final PaymentEventHandler handler;

    @RabbitListener(queues = RabbitMQConfig.QUEUE_PAYMENT_COMPLETED)
    public void onPaymentCompleted(PaymentCompletedEvent event) {
        log.info("[payment.completed] Message received");
        handler.handlePaymentCompleted(event)
                .doOnSuccess(n -> log.info("[payment.completed] Processed: {}", event.getClientName()))
                .doOnError(e -> log.error("[payment.completed] Error: {}", e.getMessage(), e))
                .subscribe();
    }

    @RabbitListener(queues = RabbitMQConfig.QUEUE_PAYMENT_OVERDUE)
    public void onPaymentOverdue(PaymentOverdueEvent event) {
        log.info("[payment.overdue] Message received");
        handler.handlePaymentOverdue(event)
                .doOnSuccess(n -> log.info("[payment.overdue] Processed: {}", event.getClientName()))
                .doOnError(e -> log.error("[payment.overdue] Error: {}", e.getMessage(), e))
                .subscribe();
    }

    @RabbitListener(queues = RabbitMQConfig.QUEUE_RECEIPT_GENERATED)
    public void onReceiptGenerated(ReceiptGeneratedEvent event) {
        log.info("[receipt.generated] Message received");
        handler.handleReceiptGenerated(event)
                .doOnSuccess(n -> log.info("[receipt.generated] Processed: {}", event.getClientName()))
                .doOnError(e -> log.error("[receipt.generated] Error: {}", e.getMessage(), e))
                .subscribe();
    }
}
