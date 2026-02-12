package pe.edu.vallegrande.msdistribution.application.dto.request;

import lombok.Builder;
import lombok.Getter;
import lombok.extern.jackson.Jacksonized;

@Getter
@Builder
@Jacksonized
public class DistributionProgramUpdateRequest {
    private final String organizationId;
    private final String scheduleId;
    private final String routeId;
    private final String zoneId;
    private final String streetId;
    private final String programDate;
    private final String plannedStartTime;
    private final String plannedEndTime;
    private final String actualStartTime;
    private final String actualEndTime;
    private final String responsibleUserId;
    private final String observations;
}
