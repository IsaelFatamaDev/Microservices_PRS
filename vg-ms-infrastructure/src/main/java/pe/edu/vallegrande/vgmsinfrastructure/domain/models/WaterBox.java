package pe.edu.vallegrande.vgmsinfrastructure.domain.models;

import lombok.Builder;
import lombok.Getter;
import pe.edu.vallegrande.vgmsinfrastructure.domain.models.valueobjects.BoxType;
import pe.edu.vallegrande.vgmsinfrastructure.domain.models.valueobjects.RecordStatus;

import java.time.LocalDateTime;

@Getter
@Builder(toBuilder = true)
public class WaterBox {

    private String id;
    private String organizationId;
    private String boxCode;
    private BoxType boxType;
    private LocalDateTime installationDate;
    private String zoneId;
    private String streetId;
    private String address;
    private String currentAssignmentId;
    private Boolean isActive;
    private RecordStatus recordStatus;
    private LocalDateTime createdAt;
    private String createdBy;
    private LocalDateTime updatedAt;
    private String updatedBy;

    public boolean isActiveRecord() {
        return recordStatus == RecordStatus.ACTIVE;
    }

    public boolean isServiceActive() {
        return isActive != null && isActive;
    }

    public WaterBox markAsDeleted(String deletedBy) {
        return this.toBuilder()
                .recordStatus(RecordStatus.INACTIVE)
                .updatedAt(LocalDateTime.now())
                .updatedBy(deletedBy)
                .build();
    }

    public WaterBox restore(String restoredBy) {
        return this.toBuilder()
                .recordStatus(RecordStatus.ACTIVE)
                .updatedAt(LocalDateTime.now())
                .updatedBy(restoredBy)
                .build();
    }

    public WaterBox suspend(String suspendedBy) {
        return this.toBuilder()
                .isActive(false)
                .updatedAt(LocalDateTime.now())
                .updatedBy(suspendedBy)
                .build();
    }

    public WaterBox reconnect(String reconnectedBy) {
        return this.toBuilder()
                .isActive(true)
                .updatedAt(LocalDateTime.now())
                .updatedBy(reconnectedBy)
                .build();
    }

    public WaterBox updateWith(WaterBox changes, String updatedBy) {
        var builder = this.toBuilder()
                .updatedAt(LocalDateTime.now())
                .updatedBy(updatedBy);
        if (changes.getBoxCode() != null) builder.boxCode(changes.getBoxCode());
        if (changes.getBoxType() != null) builder.boxType(changes.getBoxType());
        if (changes.getInstallationDate() != null) builder.installationDate(changes.getInstallationDate());
        if (changes.getZoneId() != null) builder.zoneId(changes.getZoneId());
        if (changes.getStreetId() != null) builder.streetId(changes.getStreetId());
        if (changes.getAddress() != null) builder.address(changes.getAddress());
        return builder.build();
    }
}
