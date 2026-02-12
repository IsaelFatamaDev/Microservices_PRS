package pe.edu.vallegrande.msdistribution.application.mappers;

import org.springframework.stereotype.Component;
import pe.edu.vallegrande.msdistribution.application.dto.request.DistributionProgramCreateRequest;
import pe.edu.vallegrande.msdistribution.application.dto.response.DistributionProgramResponse;
import pe.edu.vallegrande.msdistribution.domain.models.DistributionProgram;
import pe.edu.vallegrande.msdistribution.domain.models.valueobjects.RecordStatus;

import java.time.LocalDateTime;

@Component
public class DistributionProgramMapper {

    public DistributionProgram toDomain(DistributionProgramCreateRequest request) {
        return DistributionProgram.builder()
                .organizationId(request.getOrganizationId())
                .scheduleId(request.getScheduleId())
                .routeId(request.getRouteId())
                .zoneId(request.getZoneId())
                .streetId(request.getStreetId())
                .programDate(request.getProgramDate())
                .plannedStartTime(request.getPlannedStartTime())
                .plannedEndTime(request.getPlannedEndTime())
                .responsibleUserId(request.getResponsibleUserId())
                .observations(request.getObservations())
                .recordStatus(RecordStatus.ACTIVE)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    public DistributionProgramResponse toResponse(DistributionProgram domain) {
        return DistributionProgramResponse.builder()
                .id(domain.getId())
                .organizationId(domain.getOrganizationId())
                .scheduleId(domain.getScheduleId())
                .routeId(domain.getRouteId())
                .zoneId(domain.getZoneId())
                .streetId(domain.getStreetId())
                .programDate(domain.getProgramDate())
                .plannedStartTime(domain.getPlannedStartTime())
                .plannedEndTime(domain.getPlannedEndTime())
                .actualStartTime(domain.getActualStartTime())
                .actualEndTime(domain.getActualEndTime())
                .responsibleUserId(domain.getResponsibleUserId())
                .observations(domain.getObservations())
                .recordStatus(domain.getRecordStatus() != null ? domain.getRecordStatus().name() : null)
                .createdAt(domain.getCreatedAt())
                .createdBy(domain.getCreatedBy())
                .updatedAt(domain.getUpdatedAt())
                .updatedBy(domain.getUpdatedBy())
                .build();
    }
}
