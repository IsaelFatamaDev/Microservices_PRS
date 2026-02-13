package pe.edu.vallegrande.vgmsinfrastructure.application.events.external;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ServiceReconnectedEvent {
    private String waterBoxId;
    private String userId;
    private String reconnectedBy;
}
