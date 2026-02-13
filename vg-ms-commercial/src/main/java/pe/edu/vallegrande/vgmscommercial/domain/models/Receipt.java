package pe.edu.vallegrande.vgmscommercial.domain.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import pe.edu.vallegrande.vgmscommercial.domain.models.valueobjects.RecordStatus;
import pe.edu.vallegrande.vgmscommercial.domain.models.valueobjects.ReceiptStatus;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class Receipt {

    private String id;
    private String organizationId;
    private RecordStatus recordStatus;
    private LocalDateTime createdAt;
    private String createdBy;
    private LocalDateTime updatedAt;
    private String updatedBy;

    private String receiptNumber;
    private String userId;
    private Integer periodMonth;
    private Integer periodYear;
    private LocalDateTime issueDate;
    private LocalDateTime dueDate;
    private Double totalAmount;
    private Double paidAmount;
    private Double pendingAmount;
    private ReceiptStatus receiptStatus;
    private String notes;

    private List<ReceiptDetail> details;

    public boolean isPending() {
        return ReceiptStatus.PENDING.equals(receiptStatus) || ReceiptStatus.PARTIAL.equals(receiptStatus);
    }

    public boolean isPaid() {
        return ReceiptStatus.PAID.equals(receiptStatus);
    }

    public boolean isOverdue() {
        return ReceiptStatus.OVERDUE.equals(receiptStatus);
    }

    public boolean isActive() {
        return RecordStatus.ACTIVE.equals(recordStatus);
    }

    public String getPeriodDescription() {
        return String.format("%02d/%d", periodMonth, periodYear);
    }

    public Receipt markAsPaid(String updatedBy) {
        return this.toBuilder()
            .receiptStatus(ReceiptStatus.PAID)
            .paidAmount(totalAmount)
            .pendingAmount(0.0)
            .updatedAt(LocalDateTime.now())
            .updatedBy(updatedBy)
            .build();
    }

    public Receipt markAsPartial(Double paidAmount, String updatedBy) {
        double newPending = this.totalAmount - paidAmount;
        return this.toBuilder()
            .receiptStatus(ReceiptStatus.PARTIAL)
            .paidAmount(paidAmount)
            .pendingAmount(newPending)
            .updatedAt(LocalDateTime.now())
            .updatedBy(updatedBy)
            .build();
    }

    public Receipt markAsOverdue(String updatedBy) {
        return this.toBuilder()
            .receiptStatus(ReceiptStatus.OVERDUE)
            .updatedAt(LocalDateTime.now())
            .updatedBy(updatedBy)
            .build();
    }

    public Receipt markAsDeleted(String deletedBy) {
        return this.toBuilder()
            .recordStatus(RecordStatus.INACTIVE)
            .receiptStatus(ReceiptStatus.CANCELLED)
            .updatedAt(LocalDateTime.now())
            .updatedBy(deletedBy)
            .build();
    }

    public Receipt markAsActive(String restoredBy) {
        return this.toBuilder()
            .recordStatus(RecordStatus.ACTIVE)
            .receiptStatus(ReceiptStatus.PENDING)
            .updatedAt(LocalDateTime.now())
            .updatedBy(restoredBy)
            .build();
    }
}