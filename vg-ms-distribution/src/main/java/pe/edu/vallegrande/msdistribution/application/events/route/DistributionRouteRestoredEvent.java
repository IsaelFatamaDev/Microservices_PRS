package pe.edu.vallegrande.msdistribution.application.events.route;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DistributionRouteRestoredEvent {
    
    @Builder.Default
    private String eventType = "DISTRIBUTION_ROUTE_RESTORED";
    
    private String eventId;
    private LocalDateTime timestamp;
    private String correlationId;
    
    private String routeId;
    private String organizationId;
    
    private String restoredBy;
}
