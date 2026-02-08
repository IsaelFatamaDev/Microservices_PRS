package pe.edu.vallegrande.vgmsorganizations.domain.models;

import lombok.Builder;
import lombok.Getter;
import pe.edu.vallegrande.vgmsorganizations.domain.models.valueobjects.RecordStatus;

import java.time.LocalDateTime;

@Getter
@Builder(toBuilder = true)
public class Parameter {

    private String id;
    private String organizationId;
    private String parameterType;
    private String parameterValue;
    private String description;

    private RecordStatus recordStatus;
    private LocalDateTime createdAt;
    private String createdBy;
    private LocalDateTime updatedAt;
    private String updatedBy;

    public boolean isActive() {
        return recordStatus == RecordStatus.ACTIVE;
    }

    public boolean isInactive() {
        return recordStatus == RecordStatus.INACTIVE;
    }

    public Parameter markAsDeleted(String deletedBy) {
        return this.toBuilder()
            .recordStatus(RecordStatus.INACTIVE)
            .updatedAt(LocalDateTime.now())
            .updatedBy(deletedBy)
            .build();
    }

    public Parameter restore(String restoredBy) {
        return this.toBuilder()
            .recordStatus(RecordStatus.ACTIVE)
            .updatedAt(LocalDateTime.now())
            .updatedBy(restoredBy)
            .build();
    }

    public Parameter updateWith(Parameter changes, String updatedBy) {
        var builder = this.toBuilder()
            .updatedAt(LocalDateTime.now())
            .updatedBy(updatedBy);

        if(changes.getParameterType() != null) builder.parameterType(changes.getParameterType());
        if(changes.getParameterValue() != null) builder.parameterValue(changes.getParameterValue());
        if(changes.getDescription() != null) builder.description(changes.getDescription());

        return builder.build();
    }
}
