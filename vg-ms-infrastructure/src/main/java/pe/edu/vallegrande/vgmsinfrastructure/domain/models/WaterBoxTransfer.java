package pe.edu.vallegrande.vgmsinfrastructure.domain.models;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder(toBuilder = true)
public class WaterBoxTransfer {

    private String id;
    private String organizationId;
    private String waterBoxId;
    private String fromUserId;
    private String toUserId;
    private LocalDateTime transferDate;
    private Double transferFee;
    private String notes;
    private String recordStatus;
    private LocalDateTime createdAt;
    private String createdBy;
}
