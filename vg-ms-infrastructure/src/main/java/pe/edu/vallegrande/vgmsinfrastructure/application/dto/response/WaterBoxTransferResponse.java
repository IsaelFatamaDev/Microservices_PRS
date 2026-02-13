package pe.edu.vallegrande.vgmsinfrastructure.application.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class WaterBoxTransferResponse {
    private final String id;
    private final String organizationId;
    private final String waterBoxId;
    private final String fromUserId;
    private final String toUserId;
    private final LocalDateTime transferDate;
    private final Double transferFee;
    private final String notes;
    private final String recordStatus;
    private final LocalDateTime createdAt;
    private final String createdBy;
}
