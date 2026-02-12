package pe.edu.vallegrande.msdistribution.application.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class DistributionScheduleResponse {
    private final String id;
    private final String organizationId;
    private final String zoneId;
    private final String streetId;
    private final String scheduleName;
    private final List<String> daysOfWeek;
    private final String startTime;
    private final String endTime;
    private final Integer durationHours;
    private final String recordStatus;
    private final LocalDateTime createdAt;
    private final String createdBy;
    private final LocalDateTime updatedAt;
    private final String updatedBy;
}
