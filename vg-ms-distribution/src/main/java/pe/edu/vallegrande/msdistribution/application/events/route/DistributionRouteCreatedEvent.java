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
public class DistributionRouteCreatedEvent {

    @Builder.Default
    private String eventType = "DISTRIBUTION_ROUTE_CREATED";

    private String eventId;
    private LocalDateTime timestamp;
    private String correlationId;

    private String routeId;
    private String organizationId;
    private String routeName;

    private String createdBy;
}
