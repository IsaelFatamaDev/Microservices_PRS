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
public class ServiceReconnectedEvent {

     private String eventId;
     @Builder.Default
     private String eventType = "SERVICE_RECONNECTED";
     private LocalDateTime timestamp;
     private String serviceCutId;
     private String userId;
     private String organizationId;
     private String waterBoxId;
     private LocalDateTime reconnectionDate;
     private Boolean reconnectionFeePaid;
     private String reconnectedBy;
     private String correlationId;
}
