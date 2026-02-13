package pe.edu.vallegrande.vgmsinfrastructure.application.mappers;

import org.springframework.stereotype.Component;
import pe.edu.vallegrande.vgmsinfrastructure.application.dto.request.CreateWaterBoxRequest;
import pe.edu.vallegrande.vgmsinfrastructure.application.dto.request.UpdateWaterBoxRequest;
import pe.edu.vallegrande.vgmsinfrastructure.application.dto.response.WaterBoxResponse;
import pe.edu.vallegrande.vgmsinfrastructure.domain.models.WaterBox;
import pe.edu.vallegrande.vgmsinfrastructure.domain.models.valueobjects.BoxType;

import java.time.LocalDateTime;

@Component
public class WaterBoxMapper {

    public WaterBox toDomain(CreateWaterBoxRequest request) {
        return WaterBox.builder()
                .organizationId(request.getOrganizationId())
                .boxCode(request.getBoxCode())
                .boxType(BoxType.valueOf(request.getBoxType().toUpperCase()))
                .installationDate(request.getInstallationDate() != null ? LocalDateTime.parse(request.getInstallationDate()) : null)
                .zoneId(request.getZoneId())
                .streetId(request.getStreetId())
                .address(request.getAddress())
                .build();
    }

    public WaterBox toDomain(UpdateWaterBoxRequest request) {
        return WaterBox.builder()
                .boxCode(request.getBoxCode())
                .boxType(request.getBoxType() != null ? BoxType.valueOf(request.getBoxType().toUpperCase()) : null)
                .installationDate(request.getInstallationDate() != null ? LocalDateTime.parse(request.getInstallationDate()) : null)
                .zoneId(request.getZoneId())
                .streetId(request.getStreetId())
                .address(request.getAddress())
                .build();
    }

    public WaterBoxResponse toResponse(WaterBox domain) {
        return WaterBoxResponse.builder()
                .id(domain.getId())
                .organizationId(domain.getOrganizationId())
                .boxCode(domain.getBoxCode())
                .boxType(domain.getBoxType() != null ? domain.getBoxType().name() : null)
                .installationDate(domain.getInstallationDate())
                .zoneId(domain.getZoneId())
                .streetId(domain.getStreetId())
                .address(domain.getAddress())
                .currentAssignmentId(domain.getCurrentAssignmentId())
                .isActive(domain.getIsActive())
                .recordStatus(domain.getRecordStatus() != null ? domain.getRecordStatus().name() : null)
                .createdAt(domain.getCreatedAt())
                .createdBy(domain.getCreatedBy())
                .updatedAt(domain.getUpdatedAt())
                .updatedBy(domain.getUpdatedBy())
                .build();
    }
}
