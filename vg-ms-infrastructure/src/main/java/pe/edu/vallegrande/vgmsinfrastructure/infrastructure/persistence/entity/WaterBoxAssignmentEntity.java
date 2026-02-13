package pe.edu.vallegrande.vgmsinfrastructure.infrastructure.persistence.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Table("water_box_assignments")
public class WaterBoxAssignmentEntity {

    @Id
    private String id;

    @Column("organization_id")
    private String organizationId;

    @Column("water_box_id")
    private String waterBoxId;

    @Column("user_id")
    private String userId;

    @Column("assignment_date")
    private LocalDateTime assignmentDate;

    @Column("assignment_status")
    private String assignmentStatus;

    @Column("end_date")
    private LocalDateTime endDate;

    @Column("record_status")
    private String recordStatus;

    @Column("created_at")
    private LocalDateTime createdAt;

    @Column("created_by")
    private String createdBy;

    @Column("updated_at")
    private LocalDateTime updatedAt;

    @Column("updated_by")
    private String updatedBy;
}
