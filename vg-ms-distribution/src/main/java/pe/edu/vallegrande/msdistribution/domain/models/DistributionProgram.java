package pe.edu.vallegrande.msdistribution.domain.models;

import lombok.Builder;
import lombok.Getter;
import pe.edu.vallegrande.msdistribution.domain.models.valueobjects.RecordStatus;

import java.time.LocalDateTime;

@Getter
@Builder(toBuilder = true)
public class DistributionProgram {

    private String id;
    private String organizationId;
    private String scheduleId;
    private String routeId;
    private String zoneId;
    private String streetId;
    private String programDate;
    private String plannedStartTime;
    private String plannedEndTime;
    private String actualStartTime;
    private String actualEndTime;
    private String responsibleUserId;
    private String observations;

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

    public DistributionProgram markAsDeleted(String deletedBy) {
        return this.toBuilder()
                .recordStatus(RecordStatus.INACTIVE)
                .updatedAt(LocalDateTime.now())
                .updatedBy(deletedBy)
                .build();
    }

    public DistributionProgram restore(String restoredBy) {
        return this.toBuilder()
                .recordStatus(RecordStatus.ACTIVE)
                .updatedAt(LocalDateTime.now())
                .updatedBy(restoredBy)
                .build();
    }

    public DistributionProgram updateWith(DistributionProgram changes, String updatedBy) {
        var builder = this.toBuilder()
                .updatedAt(LocalDateTime.now())
                .updatedBy(updatedBy);

        if (changes.getOrganizationId() != null) builder.organizationId(changes.getOrganizationId());
        if (changes.getScheduleId() != null) builder.scheduleId(changes.getScheduleId());
        if (changes.getRouteId() != null) builder.routeId(changes.getRouteId());
        if (changes.getZoneId() != null) builder.zoneId(changes.getZoneId());
        if (changes.getStreetId() != null) builder.streetId(changes.getStreetId());
        if (changes.getProgramDate() != null) builder.programDate(changes.getProgramDate());
        if (changes.getPlannedStartTime() != null) builder.plannedStartTime(changes.getPlannedStartTime());
        if (changes.getPlannedEndTime() != null) builder.plannedEndTime(changes.getPlannedEndTime());
        if (changes.getActualStartTime() != null) builder.actualStartTime(changes.getActualStartTime());
        if (changes.getActualEndTime() != null) builder.actualEndTime(changes.getActualEndTime());
        if (changes.getResponsibleUserId() != null) builder.responsibleUserId(changes.getResponsibleUserId());
        if (changes.getObservations() != null) builder.observations(changes.getObservations());

        return builder.build();
    }
}
