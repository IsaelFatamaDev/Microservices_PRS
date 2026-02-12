package pe.edu.vallegrande.msdistribution.application.dto.request;

import lombok.Builder;
import lombok.Getter;
import lombok.extern.jackson.Jacksonized;

import java.util.List;

@Getter
@Builder
@Jacksonized
public class DistributionRouteUpdateRequest {
    private final String routeName;
    private final List<DistributionRouteCreateRequest.ZoneEntry> zones;
    private final Integer totalEstimatedDuration;
    private final String responsibleUserId;
}
