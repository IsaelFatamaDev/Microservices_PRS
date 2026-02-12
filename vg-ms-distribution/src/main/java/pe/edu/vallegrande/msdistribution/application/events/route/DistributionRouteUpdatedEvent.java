package pe.edu.vallegrande.msdistribution.application.events.route;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DistributionRouteUpdatedEvent {
    
    @Builder.Default
    private String eventType = "DISTRIBUTION_ROUTE_UPDATED";
    
    private String eventId;
    private LocalDateTime timestamp;
    private String correlationId;
    
    private String routeId;
    private String organizationId;
    private Map<String, Object> changedFields;
    
    private String updatedBy;
}
