package pe.edu.vallegrande.vgmscommercial.infrastructure.messaging;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import pe.edu.vallegrande.vgmscommercial.application.events.debt.DebtCreatedEvent;
import pe.edu.vallegrande.vgmscommercial.application.events.debt.DebtPaidEvent;
import pe.edu.vallegrande.vgmscommercial.application.events.payment.PaymentCancelledEvent;
import pe.edu.vallegrande.vgmscommercial.application.events.payment.PaymentCreatedEvent;
import pe.edu.vallegrande.vgmscommercial.application.events.receipt.ReceiptCreatedEvent;
import pe.edu.vallegrande.vgmscommercial.application.events.receipt.ReceiptPaidEvent;
import pe.edu.vallegrande.vgmscommercial.application.events.servicecut.ServiceCutExecutedEvent;
import pe.edu.vallegrande.vgmscommercial.application.events.servicecut.ServiceCutScheduledEvent;
import pe.edu.vallegrande.vgmscommercial.application.events.servicecut.ServiceReconnectedEvent;
import pe.edu.vallegrande.vgmscommercial.domain.models.*;
import pe.edu.vallegrande.vgmscommercial.domain.ports.out.ICommercialEventPublisher;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class CommercialEventPublisherImpl implements ICommercialEventPublisher {

     private final RabbitTemplate rabbitTemplate;
     private static final String EXCHANGE = "jass.events";

     @Override
     public void publishReceiptCreated(Receipt receipt, String createdBy) {
          ReceiptCreatedEvent event = ReceiptCreatedEvent.builder()
                    .eventId(UUID.randomUUID().toString())
                    .timestamp(LocalDateTime.now())
                    .receiptId(receipt.getId())
                    .receiptNumber(receipt.getReceiptNumber())
                    .userId(receipt.getUserId())
                    .organizationId(receipt.getOrganizationId())
                    .periodMonth(receipt.getPeriodMonth())
                    .periodYear(receipt.getPeriodYear())
                    .totalAmount(receipt.getTotalAmount())
                    .createdBy(createdBy)
                    .correlationId(UUID.randomUUID().toString())
                    .build();
          rabbitTemplate.convertAndSend(EXCHANGE, "receipt.created", event);
          log.info("Published receipt.created event for: {}", receipt.getReceiptNumber());
     }

     @Override
     public void publishReceiptPaid(Receipt receipt, String paidBy) {
          ReceiptPaidEvent event = ReceiptPaidEvent.builder()
                    .eventId(UUID.randomUUID().toString())
                    .timestamp(LocalDateTime.now())
                    .receiptId(receipt.getId())
                    .receiptNumber(receipt.getReceiptNumber())
                    .userId(receipt.getUserId())
                    .organizationId(receipt.getOrganizationId())
                    .totalAmount(receipt.getTotalAmount())
                    .paidAmount(receipt.getPaidAmount())
                    .paidBy(paidBy)
                    .correlationId(UUID.randomUUID().toString())
                    .build();
          rabbitTemplate.convertAndSend(EXCHANGE, "receipt.paid", event);
          log.info("Published receipt.paid event for: {}", receipt.getReceiptNumber());
     }

     @Override
     public void publishPaymentCreated(Payment payment, String createdBy) {
          PaymentCreatedEvent event = PaymentCreatedEvent.builder()
                    .eventId(UUID.randomUUID().toString())
                    .timestamp(LocalDateTime.now())
                    .paymentId(payment.getId())
                    .receiptNumber(payment.getReceiptNumber())
                    .userId(payment.getUserId())
                    .organizationId(payment.getOrganizationId())
                    .totalAmount(payment.getTotalAmount())
                    .paymentMethod(payment.getPaymentMethod().name())
                    .createdBy(createdBy)
                    .correlationId(UUID.randomUUID().toString())
                    .build();
          rabbitTemplate.convertAndSend(EXCHANGE, "payment.created", event);
          log.info("Published payment.created event for: {}", payment.getId());
     }

     @Override
     public void publishPaymentCancelled(Payment payment, String cancelledBy) {
          PaymentCancelledEvent event = PaymentCancelledEvent.builder()
                    .eventId(UUID.randomUUID().toString())
                    .timestamp(LocalDateTime.now())
                    .paymentId(payment.getId())
                    .receiptNumber(payment.getReceiptNumber())
                    .userId(payment.getUserId())
                    .organizationId(payment.getOrganizationId())
                    .totalAmount(payment.getTotalAmount())
                    .cancelledBy(cancelledBy)
                    .correlationId(UUID.randomUUID().toString())
                    .build();
          rabbitTemplate.convertAndSend(EXCHANGE, "payment.cancelled", event);
          log.info("Published payment.cancelled event for: {}", payment.getId());
     }

     @Override
     public void publishDebtCreated(Debt debt, String createdBy) {
          DebtCreatedEvent event = DebtCreatedEvent.builder()
                    .eventId(UUID.randomUUID().toString())
                    .timestamp(LocalDateTime.now())
                    .debtId(debt.getId())
                    .userId(debt.getUserId())
                    .organizationId(debt.getOrganizationId())
                    .periodMonth(debt.getPeriodMonth())
                    .periodYear(debt.getPeriodYear())
                    .originalAmount(debt.getOriginalAmount())
                    .createdBy(createdBy)
                    .correlationId(UUID.randomUUID().toString())
                    .build();
          rabbitTemplate.convertAndSend(EXCHANGE, "debt.created", event);
          log.info("Published debt.created event for: {}", debt.getId());
     }

     @Override
     public void publishDebtPaid(Debt debt, String paidBy) {
          DebtPaidEvent event = DebtPaidEvent.builder()
                    .eventId(UUID.randomUUID().toString())
                    .timestamp(LocalDateTime.now())
                    .debtId(debt.getId())
                    .userId(debt.getUserId())
                    .organizationId(debt.getOrganizationId())
                    .originalAmount(debt.getOriginalAmount())
                    .paidBy(paidBy)
                    .correlationId(UUID.randomUUID().toString())
                    .build();
          rabbitTemplate.convertAndSend(EXCHANGE, "debt.paid", event);
          log.info("Published debt.paid event for: {}", debt.getId());
     }

     @Override
     public void publishServiceCutScheduled(ServiceCut serviceCut, String scheduledBy) {
          ServiceCutScheduledEvent event = ServiceCutScheduledEvent.builder()
                    .eventId(UUID.randomUUID().toString())
                    .timestamp(LocalDateTime.now())
                    .serviceCutId(serviceCut.getId())
                    .userId(serviceCut.getUserId())
                    .organizationId(serviceCut.getOrganizationId())
                    .waterBoxId(serviceCut.getWaterBoxId())
                    .scheduledDate(serviceCut.getScheduledDate())
                    .cutReason(serviceCut.getCutReason().name())
                    .debtAmount(serviceCut.getDebtAmount())
                    .scheduledBy(scheduledBy)
                    .correlationId(UUID.randomUUID().toString())
                    .build();
          rabbitTemplate.convertAndSend(EXCHANGE, "servicecut.scheduled", event);
          log.info("Published servicecut.scheduled event for: {}", serviceCut.getId());
     }

     @Override
     public void publishServiceCutExecuted(ServiceCut serviceCut, String executedBy) {
          ServiceCutExecutedEvent event = ServiceCutExecutedEvent.builder()
                    .eventId(UUID.randomUUID().toString())
                    .timestamp(LocalDateTime.now())
                    .serviceCutId(serviceCut.getId())
                    .userId(serviceCut.getUserId())
                    .organizationId(serviceCut.getOrganizationId())
                    .waterBoxId(serviceCut.getWaterBoxId())
                    .executedDate(serviceCut.getExecutedDate())
                    .executedBy(executedBy)
                    .correlationId(UUID.randomUUID().toString())
                    .build();
          rabbitTemplate.convertAndSend(EXCHANGE, "servicecut.executed", event);
          log.info("Published servicecut.executed event for: {}", serviceCut.getId());
     }

     @Override
     public void publishServiceReconnected(ServiceCut serviceCut, String reconnectedBy) {
          ServiceReconnectedEvent event = ServiceReconnectedEvent.builder()
                    .eventId(UUID.randomUUID().toString())
                    .timestamp(LocalDateTime.now())
                    .serviceCutId(serviceCut.getId())
                    .userId(serviceCut.getUserId())
                    .organizationId(serviceCut.getOrganizationId())
                    .waterBoxId(serviceCut.getWaterBoxId())
                    .reconnectionDate(serviceCut.getReconnectionDate())
                    .reconnectionFeePaid(serviceCut.getReconnectionFeePaid())
                    .reconnectedBy(reconnectedBy)
                    .correlationId(UUID.randomUUID().toString())
                    .build();
          rabbitTemplate.convertAndSend(EXCHANGE, "service.reconnected", event);
          log.info("Published service.reconnected event for: {}", serviceCut.getId());
     }
}
