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
public class ReceiptRestoredEvent {

     private String eventId;
     @Builder.Default
     private String eventType = "RECEIPT_RESTORED";
     private LocalDateTime timestamp;
     private String receiptId;
     private String receiptNumber;
     private String organizationId;
     private String restoredBy;
     private String correlationId;
}
