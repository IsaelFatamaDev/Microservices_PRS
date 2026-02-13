package pe.edu.vallegrande.vgmscommercial.application.events.debt;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DebtPaidEvent {

     private String eventId;
     @Builder.Default
     private String eventType = "DEBT_PAID";
     private LocalDateTime timestamp;
     private String debtId;
     private String userId;
     private String organizationId;
     private Double originalAmount;
     private Double paidAmount;
     private String paymentId;
     private String paidBy;
     private String correlationId;
}
