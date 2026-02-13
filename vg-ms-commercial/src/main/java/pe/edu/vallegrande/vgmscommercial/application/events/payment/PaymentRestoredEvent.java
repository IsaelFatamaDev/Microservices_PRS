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
public class PaymentRestoredEvent {

     private String eventId;
     @Builder.Default
     private String eventType = "PAYMENT_RESTORED";
     private LocalDateTime timestamp;
     private String paymentId;
     private String organizationId;
     private String restoredBy;
     private String correlationId;
}
