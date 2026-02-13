package pe.edu.vallegrande.vgmscommercial.application.events.payment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentCancelledEvent {

     private String eventId;
     @Builder.Default
     private String eventType = "PAYMENT_CANCELLED";
     private LocalDateTime timestamp;
     private String paymentId;
     private String receiptNumber;
     private String userId;
     private String organizationId;
     private Double totalAmount;
     private String reason;
     private String cancelledBy;
     private String correlationId;
}
