package pe.edu.vallegrande.vgmscommercial.domain.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import pe.edu.vallegrande.vgmscommercial.domain.models.valueobjects.DebtStatus;
import pe.edu.vallegrande.vgmscommercial.domain.models.valueobjects.RecordStatus;

import java.time.LocalDateTime;

@Getter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class Debt {

    private String id;
    private String organizationId;
    private RecordStatus recordStatus;
    private LocalDateTime createdAt;
    private String createdBy;
    private LocalDateTime updatedAt;
    private String updatedBy;

    private String userId;
    private Integer periodMonth;
    private Integer periodYear;
    private Double originalAmount;
    private Double pendingAmount;
    private Double lateFee;
    private DebtStatus debtStatus;
    private LocalDateTime dueDate;

    public boolean isPending() {
        return DebtStatus.PENDING.equals(debtStatus);
    }

    public boolean isPartial() {
        return DebtStatus.PARTIAL.equals(debtStatus);
    }

    public boolean isPaid() {
        return DebtStatus.PAID.equals(debtStatus);
    }

    public boolean isActive() {
        return RecordStatus.ACTIVE.equals(recordStatus);
    }

    public Double getTotalAmount() {
        return pendingAmount + (lateFee != null ? lateFee : 0.0);
    }

    public String getPeriodDescription() {
        return String.format("%02d/%d", periodMonth, periodYear);
    }

    public Debt applyLateFee(Double fee, String updatedBy) {
        return this.toBuilder()
            .lateFee((this.lateFee != null ? this.lateFee : 0.0) + fee)
            .updatedAt(LocalDateTime.now())
            .updatedBy(updatedBy)
            .build();
    }

    public Debt applyPayment(Double amount, String updatedBy) {
        double newPending = this.pendingAmount - amount;
        DebtStatus newStatus = newPending <= 0 ? DebtStatus.PAID : DebtStatus.PARTIAL;

        return this.toBuilder()
            .pendingAmount(Math.max(0, newPending))
            .debtStatus(newStatus)
            .updatedAt(LocalDateTime.now())
            .updatedBy(updatedBy)
            .build();
    }

    public Debt markAsPaid(String updatedBy) {
        return this.toBuilder()
            .pendingAmount(0.0)
            .debtStatus(DebtStatus.PAID)
            .updatedAt(LocalDateTime.now())
            .updatedBy(updatedBy)
            .build();
    }

    public Debt markAsDeleted(String deletedBy) {
        return this.toBuilder()
            .recordStatus(RecordStatus.INACTIVE)
            .debtStatus(DebtStatus.CANCELLED)
            .updatedAt(LocalDateTime.now())
            .updatedBy(deletedBy)
            .build();
    }

    public Debt markAsActive(String restoredBy) {
        return this.toBuilder()
            .recordStatus(RecordStatus.ACTIVE)
            .debtStatus(DebtStatus.PENDING)
            .updatedAt(LocalDateTime.now())
            .updatedBy(restoredBy)
            .build();
    }
}