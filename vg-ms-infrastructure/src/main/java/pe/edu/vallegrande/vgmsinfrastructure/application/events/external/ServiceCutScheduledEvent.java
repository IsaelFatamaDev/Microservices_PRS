package pe.edu.vallegrande.vgmsinfrastructure.application.events.external;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ServiceCutScheduledEvent {
    private String waterBoxId;
    private String userId;
    private String reason;
    private String scheduledBy;
}
