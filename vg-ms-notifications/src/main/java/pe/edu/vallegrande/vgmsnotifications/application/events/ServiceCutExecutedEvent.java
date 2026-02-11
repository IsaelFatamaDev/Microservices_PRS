package pe.edu.vallegrande.vgmsnotifications.application.events;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ServiceCutExecutedEvent {

    private String eventId;
    private String userId;
    private String clientName;
    private String phoneNumber;
    private String cutDate;
    private String debtAmount;
    private String timestamp;
}
