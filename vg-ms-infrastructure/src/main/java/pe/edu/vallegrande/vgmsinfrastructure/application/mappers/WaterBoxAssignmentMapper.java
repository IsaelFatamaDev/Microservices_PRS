package pe.edu.vallegrande.vgmsinfrastructure.application.mappers;

import org.springframework.stereotype.Component;
import pe.edu.vallegrande.vgmsinfrastructure.application.dto.response.WaterBoxAssignmentResponse;
import pe.edu.vallegrande.vgmsinfrastructure.domain.models.WaterBoxAssignment;

@Component
public class WaterBoxAssignmentMapper {

    public WaterBoxAssignmentResponse toResponse(WaterBoxAssignment domain) {
        return WaterBoxAssignmentResponse.builder()
                .id(domain.getId())
                .organizationId(domain.getOrganizationId())
                .waterBoxId(domain.getWaterBoxId())
                .userId(domain.getUserId())
                .assignmentDate(domain.getAssignmentDate())
                .assignmentStatus(domain.getAssignmentStatus() != null ? domain.getAssignmentStatus().name() : null)
                .endDate(domain.getEndDate())
                .recordStatus(domain.getRecordStatus() != null ? domain.getRecordStatus().name() : null)
                .createdAt(domain.getCreatedAt())
                .createdBy(domain.getCreatedBy())
                .updatedAt(domain.getUpdatedAt())
                .updatedBy(domain.getUpdatedBy())
                .build();
    }
}
