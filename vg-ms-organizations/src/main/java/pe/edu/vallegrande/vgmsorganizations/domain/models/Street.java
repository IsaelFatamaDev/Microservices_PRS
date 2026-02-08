package pe.edu.vallegrande.vgmsorganizations.domain.models;

import lombok.Builder;
import lombok.Getter;
import pe.edu.vallegrande.vgmsorganizations.domain.models.valueobjects.RecordStatus;
import pe.edu.vallegrande.vgmsorganizations.domain.models.valueobjects.StreetType;

import java.time.LocalDateTime;

@Getter
@Builder(toBuilder = true)
public class Street {

    private String id;
    private String organizationId;
    private String zoneId;
    private StreetType streetType;
    private String streetName;

    private RecordStatus recordStatus;
    private LocalDateTime createdAt;
    private String createdBy;
    private LocalDateTime updatedAt;
    private String updatedBy;

    public String getFullStreetName() {
        return streetType.getPrefix() + " " + streetName;
    }

    public boolean isActive() {
        return recordStatus == RecordStatus.ACTIVE;
    }

    public boolean isInactive() {
        return recordStatus == RecordStatus.INACTIVE;
    }

    public Street markAsDeleted(String deletedBy) {
        return this.toBuilder()
            .recordStatus(RecordStatus.INACTIVE)
            .updatedAt(LocalDateTime.now())
            .updatedBy(deletedBy)
            .build();
    }

    public Street restore(String restoredBy) {
        return this.toBuilder()
            .recordStatus(RecordStatus.ACTIVE)
            .updatedAt(LocalDateTime.now())
            .updatedBy(restoredBy)
            .build();
    }

    public Street updateWith(Street changes, String updatedBy) {
        var builder = this.toBuilder()
            .updatedAt(LocalDateTime.now())
            .updatedBy(updatedBy);

        if (changes.getStreetType() != null) builder.streetType(changes.getStreetType());
        if (changes.getStreetName() != null) builder.streetName(changes.getStreetName());

        return builder.build();
    }
}
