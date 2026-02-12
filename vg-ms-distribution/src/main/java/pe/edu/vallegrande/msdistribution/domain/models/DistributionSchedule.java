package pe.edu.vallegrande.msdistribution.domain.models;

import lombok.Builder;
import lombok.Getter;
import pe.edu.vallegrande.msdistribution.domain.models.valueobjects.RecordStatus;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder(toBuilder = true)
public class DistributionSchedule {

    private String id;
    private String organizationId;
    private String zoneId;
    private String streetId;
    private String scheduleName;
    private List<String> daysOfWeek;
    private String startTime;
    private String endTime;
    private Integer durationHours;

    private RecordStatus recordStatus;
    private LocalDateTime createdAt;
    private String createdBy;
    private LocalDateTime updatedAt;
    private String updatedBy;

    // ========== MÉTODOS DE NEGOCIO ==========

    public boolean isActive() {
        return recordStatus == RecordStatus.ACTIVE;
    }

    public boolean isInactive() {
        return recordStatus == RecordStatus.INACTIVE;
    }

    public DistributionSchedule markAsDeleted(String deletedBy) {
        return this.toBuilder()
                .recordStatus(RecordStatus.INACTIVE)
                .updatedAt(LocalDateTime.now())
                .updatedBy(deletedBy)
                .build();
    }

    public DistributionSchedule restore(String restoredBy) {
        return this.toBuilder()
                .recordStatus(RecordStatus.ACTIVE)
                .updatedAt(LocalDateTime.now())
                .updatedBy(restoredBy)
                .build();
    }

    public DistributionSchedule updateWith(DistributionSchedule changes, String updatedBy) {
        var builder = this.toBuilder()
                .updatedAt(LocalDateTime.now())
                .updatedBy(updatedBy);

        if (changes.getZoneId() != null) builder.zoneId(changes.getZoneId());
        if (changes.getStreetId() != null) builder.streetId(changes.getStreetId());
        if (changes.getScheduleName() != null) builder.scheduleName(changes.getScheduleName());
        if (changes.getDaysOfWeek() != null) builder.daysOfWeek(changes.getDaysOfWeek());
        if (changes.getStartTime() != null) builder.startTime(changes.getStartTime());
        if (changes.getEndTime() != null) builder.endTime(changes.getEndTime());
        if (changes.getDurationHours() != null) builder.durationHours(changes.getDurationHours());

        return builder.build();
    }
}
