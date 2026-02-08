package pe.edu.vallegrande.vgmsorganizations.domain.models;

import lombok.Builder;
import lombok.Getter;
import pe.edu.vallegrande.vgmsorganizations.domain.models.valueobjects.RecordStatus;

import java.time.LocalDateTime;

@Getter
@Builder(toBuilder = true)
public class Fare {

    private String id;
    private String organizationId;
    private String fareType;
    private Double amount;
    private String description;
    private LocalDateTime validFrom;
    private LocalDateTime validTo;

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

    public boolean isCurrentlyValid(){
        LocalDateTime now = LocalDateTime.now();
        boolean afterStart = validFrom == null || !now.isBefore(validFrom);
        boolean beforeEnd = validTo == null || !now.isAfter(validTo);
        return afterStart && beforeEnd && isActive();
    }

    public void validateAmount() {
        if(amount == null || amount <= 0){
            throw new IllegalArgumentException("Fare amount must be greater than 0");
        }
    }

    public Fare markAsDeleted(String deletedBy) {
        return this.toBuilder()
            .recordStatus(RecordStatus.INACTIVE)
            .updatedAt(LocalDateTime.now())
            .updatedBy(deletedBy)
            .build();
    }

    public Fare restore(String restoredBy) {
        return this.toBuilder()
            .recordStatus(RecordStatus.ACTIVE)
            .updatedAt(LocalDateTime.now())
            .updatedBy(restoredBy)
            .build();
    }

    public Fare updateWith(Fare changes, String updatedBy) {
        var builder = this.toBuilder()
            .updatedAt(LocalDateTime.now())
            .updatedBy(updatedBy);

        if(changes.getFareType() != null) builder.fareType(changes.getFareType());
        if(changes.getAmount() != null) builder.amount(changes.getAmount());
        if(changes.getDescription() != null) builder.description(changes.getDescription());
        if(changes.getValidFrom() != null) builder.validFrom(changes.getValidFrom());
        if(changes.getValidTo() != null) builder.validTo(changes.getValidTo());

        return builder.build();
    }
}
