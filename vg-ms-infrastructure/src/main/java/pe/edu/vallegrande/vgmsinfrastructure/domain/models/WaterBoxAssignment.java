package pe.edu.vallegrande.vgmsinfrastructure.domain.models;

import lombok.Builder;
import lombok.Getter;
import pe.edu.vallegrande.vgmsinfrastructure.domain.models.valueobjects.AssignmentStatus;
import pe.edu.vallegrande.vgmsinfrastructure.domain.models.valueobjects.RecordStatus;

import java.time.LocalDateTime;

@Getter
@Builder(toBuilder = true)
public class WaterBoxAssignment {

    private String id;
    private String organizationId;
    private String waterBoxId;
    private String userId;
    private LocalDateTime assignmentDate;
    private AssignmentStatus assignmentStatus;
    private LocalDateTime endDate;
    private RecordStatus recordStatus;
    private LocalDateTime createdAt;
    private String createdBy;
    private LocalDateTime updatedAt;
    private String updatedBy;

    public boolean isActive() {
        return assignmentStatus == AssignmentStatus.ACTIVE;
    }

    public WaterBoxAssignment markAsTransferred(String updatedBy) {
        return this.toBuilder()
                .assignmentStatus(AssignmentStatus.TRANSFERRED)
                .endDate(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .updatedBy(updatedBy)
                .build();
    }

    public WaterBoxAssignment deactivate(String updatedBy) {
        return this.toBuilder()
                .assignmentStatus(AssignmentStatus.INACTIVE)
                .endDate(LocalDateTime.now())
                .recordStatus(RecordStatus.INACTIVE)
                .updatedAt(LocalDateTime.now())
                .updatedBy(updatedBy)
                .build();
    }
}
