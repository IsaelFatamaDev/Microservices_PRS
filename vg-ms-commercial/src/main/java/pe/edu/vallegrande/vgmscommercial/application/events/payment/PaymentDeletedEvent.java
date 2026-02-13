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
public class PaymentDeletedEvent {

     private String eventId;
     @Builder.Default
     private String eventType = "PAYMENT_DELETED";
     private LocalDateTime timestamp;
     private String paymentId;
     private String organizationId;
     private String deletedBy;
     private String correlationId;
}
