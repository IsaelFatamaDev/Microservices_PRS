package pe.edu.vallegrande.msdistribution.application.mappers;

import org.springframework.stereotype.Component;
import pe.edu.vallegrande.msdistribution.application.dto.request.DistributionRouteCreateRequest;
import pe.edu.vallegrande.msdistribution.application.dto.response.DistributionRouteResponse;
import pe.edu.vallegrande.msdistribution.domain.models.DistributionRoute;
import pe.edu.vallegrande.msdistribution.domain.models.valueobjects.RecordStatus;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@Component
public class DistributionRouteMapper {

    public DistributionRoute toDomain(DistributionRouteCreateRequest request) {
        return DistributionRoute.builder()
                .organizationId(request.getOrganizationId())
                .routeName(request.getRouteName())
                .zones(request.getZones() != null ? request.getZones().stream()
                        .map(z -> DistributionRoute.ZoneOrder.builder()
                                .zoneId(z.getZoneId())
                                .order(z.getOrder())
                                .estimatedDuration(z.getEstimatedDuration())
                                .build())
                        .collect(Collectors.toList()) : null)
                .totalEstimatedDuration(request.getTotalEstimatedDuration())
                .responsibleUserId(request.getResponsibleUserId())
                .recordStatus(RecordStatus.ACTIVE)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    public DistributionRouteResponse toResponse(DistributionRoute domain) {
        return DistributionRouteResponse.builder()
                .id(domain.getId())
                .organizationId(domain.getOrganizationId())
                .routeName(domain.getRouteName())
                .zones(domain.getZones() != null ? domain.getZones().stream()
                        .map(z -> DistributionRouteResponse.ZoneOrderResponse.builder()
                                .zoneId(z.getZoneId())
                                .order(z.getOrder())
                                .estimatedDuration(z.getEstimatedDuration())
                                .build())
                        .collect(Collectors.toList()) : null)
                .totalEstimatedDuration(domain.getTotalEstimatedDuration())
                .responsibleUserId(domain.getResponsibleUserId())
                .recordStatus(domain.getRecordStatus() != null ? domain.getRecordStatus().name() : null)
                .createdAt(domain.getCreatedAt())
                .createdBy(domain.getCreatedBy())
                .updatedAt(domain.getUpdatedAt())
                .updatedBy(domain.getUpdatedBy())
                .build();
    }
}
