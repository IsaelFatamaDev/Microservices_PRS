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
public class ServiceCutDeletedEvent {

     private String eventId;
     @Builder.Default
     private String eventType = "SERVICE_CUT_DELETED";
     private LocalDateTime timestamp;
     private String serviceCutId;
     private String organizationId;
     private String deletedBy;
     private String correlationId;
}
