package pe.edu.vallegrande.vgmsinfrastructure.application.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class WaterBoxResponse {
    private final String id;
    private final String organizationId;
    private final String boxCode;
    private final String boxType;
    private final LocalDateTime installationDate;
    private final String zoneId;
    private final String streetId;
    private final String address;
    private final String currentAssignmentId;
    private final Boolean isActive;
    private final String recordStatus;
    private final LocalDateTime createdAt;
    private final String createdBy;
    private final LocalDateTime updatedAt;
    private final String updatedBy;
}
