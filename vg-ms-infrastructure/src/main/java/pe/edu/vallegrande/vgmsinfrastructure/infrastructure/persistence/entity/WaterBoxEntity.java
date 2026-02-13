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
@Table("water_boxes")
public class WaterBoxEntity {

    @Id
    private String id;

    @Column("organization_id")
    private String organizationId;

    @Column("box_code")
    private String boxCode;

    @Column("box_type")
    private String boxType;

    @Column("installation_date")
    private LocalDateTime installationDate;

    @Column("zone_id")
    private String zoneId;

    @Column("street_id")
    private String streetId;

    @Column("address")
    private String address;

    @Column("current_assignment_id")
    private String currentAssignmentId;

    @Column("is_active")
    private Boolean isActive;

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
