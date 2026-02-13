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
public class ReceiptCreatedEvent {

     private String eventId;
     @Builder.Default
     private String eventType = "RECEIPT_CREATED";
     private LocalDateTime timestamp;
     private String receiptId;
     private String receiptNumber;
     private String userId;
     private String organizationId;
     private Integer periodMonth;
     private Integer periodYear;
     private Double totalAmount;
     private String createdBy;
     private String correlationId;
}
