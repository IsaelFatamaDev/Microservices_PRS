package pe.edu.vallegrande.vgmscommercial.domain.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import pe.edu.vallegrande.vgmscommercial.domain.models.valueobjects.PettyCashStatus;
import pe.edu.vallegrande.vgmscommercial.domain.models.valueobjects.RecordStatus;

import java.time.LocalDateTime;

@Getter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class PettyCash {

    private String id;
    private String organizationId;
    private RecordStatus recordStatus;
    private LocalDateTime createdAt;
    private String createdBy;
    private LocalDateTime updatedAt;
    private String updatedBy;

    private String responsibleUserId;
    private Double currentBalance;
    private Double maxAmountLimit;
    private PettyCashStatus pettyCashStatus;

    public boolean isActive() {
        return RecordStatus.ACTIVE.equals(recordStatus) && PettyCashStatus.ACTIVE.equals(pettyCashStatus);
    }

    public boolean isClosed() {
        return PettyCashStatus.CLOSED.equals(pettyCashStatus);
    }

    public boolean hasAvailableBalance(Double amount) {
        return currentBalance >= amount;
    }

    public boolean exceedsLimit(Double amount) {
        return (currentBalance + amount) > maxAmountLimit;
    }

    public PettyCash addFunds(Double amount, String updatedBy) {
        return this.toBuilder()
            .currentBalance(this.currentBalance + amount)
            .updatedAt(LocalDateTime.now())
            .updatedBy(updatedBy)
            .build();
    }

    public PettyCash withdrawFunds(Double amount, String updatedBy) {
        if (!hasAvailableBalance(amount)) {
            throw new IllegalArgumentException("Insufficient balance in petty cash");
        }
        return this.toBuilder()
            .currentBalance(this.currentBalance - amount)
            .updatedAt(LocalDateTime.now())
            .updatedBy(updatedBy)
            .build();
    }

    public PettyCash close(String closedBy) {
        return this.toBuilder()
            .pettyCashStatus(PettyCashStatus.CLOSED)
            .updatedAt(LocalDateTime.now())
            .updatedBy(closedBy)
            .build();
    }

    public PettyCash markAsDeleted(String deletedBy) {
        return this.toBuilder()
            .recordStatus(RecordStatus.INACTIVE)
            .pettyCashStatus(PettyCashStatus.CLOSED)
            .updatedAt(LocalDateTime.now())
            .updatedBy(deletedBy)
            .build();
    }

    public PettyCash markAsActive(String restoredBy) {
        return this.toBuilder()
            .recordStatus(RecordStatus.ACTIVE)
            .pettyCashStatus(PettyCashStatus.ACTIVE)
            .updatedAt(LocalDateTime.now())
            .updatedBy(restoredBy)
            .build();
    }
}