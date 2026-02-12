package pe.edu.vallegrande.msdistribution.application.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class DistributionProgramResponse {
    private final String id;
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
    private final String recordStatus;
    private final LocalDateTime createdAt;
    private final String createdBy;
    private final LocalDateTime updatedAt;
    private final String updatedBy;
}
