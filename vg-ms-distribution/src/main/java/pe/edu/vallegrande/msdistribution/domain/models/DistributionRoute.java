package pe.edu.vallegrande.msdistribution.domain.models;

import lombok.Builder;
import lombok.Getter;
import pe.edu.vallegrande.msdistribution.domain.models.valueobjects.RecordStatus;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder(toBuilder = true)
public class DistributionRoute {

    private String id;
    private String organizationId;
    private String routeName;
    private List<ZoneOrder> zones;
    private int totalEstimatedDuration;
    private String responsibleUserId;

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

    public DistributionRoute markAsDeleted(String deletedBy) {
        return this.toBuilder()
                .recordStatus(RecordStatus.INACTIVE)
                .updatedAt(LocalDateTime.now())
                .updatedBy(deletedBy)
                .build();
    }

    public DistributionRoute restore(String restoredBy) {
        return this.toBuilder()
                .recordStatus(RecordStatus.ACTIVE)
                .updatedAt(LocalDateTime.now())
                .updatedBy(restoredBy)
                .build();
    }

    public DistributionRoute updateWith(DistributionRoute changes, String updatedBy) {
        var builder = this.toBuilder()
                .updatedAt(LocalDateTime.now())
                .updatedBy(updatedBy);

        if (changes.getRouteName() != null) builder.routeName(changes.getRouteName());
        if (changes.getZones() != null) builder.zones(changes.getZones());
        if (changes.getResponsibleUserId() != null) builder.responsibleUserId(changes.getResponsibleUserId());

        return builder.build();
    }

    @Getter
    @Builder(toBuilder = true)
    public static class ZoneOrder {
        private String zoneId;
        private int order;
        private int estimatedDuration;
    }
}
