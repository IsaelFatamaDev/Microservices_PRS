package pe.edu.vallegrande.vgmsinfrastructure.application.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class WaterBoxAssignmentResponse {
    private final String id;
    private final String organizationId;
    private final String waterBoxId;
    private final String userId;
    private final LocalDateTime assignmentDate;
    private final String assignmentStatus;
    private final LocalDateTime endDate;
    private final String recordStatus;
    private final LocalDateTime createdAt;
    private final String createdBy;
    private final LocalDateTime updatedAt;
    private final String updatedBy;
}
