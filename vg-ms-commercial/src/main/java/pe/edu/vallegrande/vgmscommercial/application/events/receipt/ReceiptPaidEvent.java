package pe.edu.vallegrande.vgmscommercial.application.events.receipt;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReceiptPaidEvent {

     private String eventId;
     @Builder.Default
     private String eventType = "RECEIPT_PAID";
     private LocalDateTime timestamp;
     private String receiptId;
     private String receiptNumber;
     private String userId;
     private String organizationId;
     private Double totalAmount;
     private Double paidAmount;
     private String paymentId;
     private String paidBy;
     private String correlationId;
}
