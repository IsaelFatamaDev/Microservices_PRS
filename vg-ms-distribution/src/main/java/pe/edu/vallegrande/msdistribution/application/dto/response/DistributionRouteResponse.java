package pe.edu.vallegrande.msdistribution.application.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class DistributionRouteResponse {
    private final String id;
    private final String organizationId;
    private final String routeName;
    private final List<ZoneOrderResponse> zones;
    private final int totalEstimatedDuration;
    private final String responsibleUserId;
    private final String recordStatus;
    private final LocalDateTime createdAt;
    private final String createdBy;
    private final LocalDateTime updatedAt;
    private final String updatedBy;

    @Getter
    @Builder
    public static class ZoneOrderResponse {
        private final String zoneId;
        private final int order;
        private final int estimatedDuration;
    }
}
