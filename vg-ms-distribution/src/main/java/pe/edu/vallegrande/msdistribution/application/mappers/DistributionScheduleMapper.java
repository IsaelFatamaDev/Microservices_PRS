package pe.edu.vallegrande.msdistribution.application.mappers;

import org.springframework.stereotype.Component;
import pe.edu.vallegrande.msdistribution.application.dto.request.DistributionScheduleCreateRequest;
import pe.edu.vallegrande.msdistribution.application.dto.response.DistributionScheduleResponse;
import pe.edu.vallegrande.msdistribution.domain.models.DistributionSchedule;
import pe.edu.vallegrande.msdistribution.domain.models.valueobjects.RecordStatus;

import java.time.LocalDateTime;

@Component
public class DistributionScheduleMapper {

    public DistributionSchedule toDomain(DistributionScheduleCreateRequest request) {
        return DistributionSchedule.builder()
                .organizationId(request.getOrganizationId())
                .zoneId(request.getZoneId())
                .streetId(request.getStreetId())
                .scheduleName(request.getScheduleName())
                .daysOfWeek(request.getDaysOfWeek())
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .durationHours(request.getDurationHours())
                .recordStatus(RecordStatus.ACTIVE)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    public DistributionScheduleResponse toResponse(DistributionSchedule domain) {
        return DistributionScheduleResponse.builder()
                .id(domain.getId())
                .organizationId(domain.getOrganizationId())
                .zoneId(domain.getZoneId())
                .streetId(domain.getStreetId())
                .scheduleName(domain.getScheduleName())
                .daysOfWeek(domain.getDaysOfWeek())
                .startTime(domain.getStartTime())
                .endTime(domain.getEndTime())
                .durationHours(domain.getDurationHours())
                .recordStatus(domain.getRecordStatus() != null ? domain.getRecordStatus().name() : null)
                .createdAt(domain.getCreatedAt())
                .createdBy(domain.getCreatedBy())
                .updatedAt(domain.getUpdatedAt())
                .updatedBy(domain.getUpdatedBy())
                .build();
    }
}
