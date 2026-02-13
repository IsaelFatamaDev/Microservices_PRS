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
public class ReceiptDeletedEvent {

     private String eventId;
     @Builder.Default
     private String eventType = "RECEIPT_DELETED";
     private LocalDateTime timestamp;
     private String receiptId;
     private String receiptNumber;
     private String organizationId;
     private String previousStatus;
     private String deletedBy;
     private String correlationId;
}
