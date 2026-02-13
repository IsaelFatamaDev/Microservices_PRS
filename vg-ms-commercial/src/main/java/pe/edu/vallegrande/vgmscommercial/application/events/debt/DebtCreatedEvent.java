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
public class DebtCreatedEvent {

     private String eventId;
     @Builder.Default
     private String eventType = "DEBT_CREATED";
     private LocalDateTime timestamp;
     private String debtId;
     private String userId;
     private String organizationId;
     private Integer periodMonth;
     private Integer periodYear;
     private Double originalAmount;
     private String createdBy;
     private String correlationId;
}
