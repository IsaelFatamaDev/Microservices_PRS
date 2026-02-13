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
public class DebtDeletedEvent {

     private String eventId;
     @Builder.Default
     private String eventType = "DEBT_DELETED";
     private LocalDateTime timestamp;
     private String debtId;
     private String organizationId;
     private String deletedBy;
     private String correlationId;
}
