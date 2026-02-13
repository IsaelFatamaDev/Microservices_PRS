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
@Table("water_box_transfers")
public class WaterBoxTransferEntity {

    @Id
    private String id;

    @Column("organization_id")
    private String organizationId;

    @Column("water_box_id")
    private String waterBoxId;

    @Column("from_user_id")
    private String fromUserId;

    @Column("to_user_id")
    private String toUserId;

    @Column("transfer_date")
    private LocalDateTime transferDate;

    @Column("transfer_fee")
    private Double transferFee;

    @Column("notes")
    private String notes;

    @Column("record_status")
    private String recordStatus;

    @Column("created_at")
    private LocalDateTime createdAt;

    @Column("created_by")
    private String createdBy;
}
