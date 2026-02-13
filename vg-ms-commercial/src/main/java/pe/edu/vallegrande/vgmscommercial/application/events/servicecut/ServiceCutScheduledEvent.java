package pe.edu.vallegrande.vgmscommercial.application.events.servicecut;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ServiceCutScheduledEvent {

     private String eventId;
     @Builder.Default
     private String eventType = "SERVICE_CUT_SCHEDULED";
     private LocalDateTime timestamp;
     private String serviceCutId;
     private String userId;
     private String organizationId;
     private String waterBoxId;
     private LocalDateTime scheduledDate;
     private String cutReason;
     private Double debtAmount;
     private String scheduledBy;
     private String correlationId;
}
