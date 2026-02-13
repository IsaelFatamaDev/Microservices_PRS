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
public class PaymentCreatedEvent {

     private String eventId;
     @Builder.Default
     private String eventType = "PAYMENT_CREATED";
     private LocalDateTime timestamp;
     private String paymentId;
     private String receiptNumber;
     private String userId;
     private String organizationId;
     private Double totalAmount;
     private String paymentMethod;
     private String createdBy;
     private String correlationId;
}
