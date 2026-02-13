package pe.edu.vallegrande.vgmscommercial.domain.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import pe.edu.vallegrande.vgmscommercial.domain.models.valueobjects.PaymentMethod;
import pe.edu.vallegrande.vgmscommercial.domain.models.valueobjects.PaymentStatus;
import pe.edu.vallegrande.vgmscommercial.domain.models.valueobjects.RecordStatus;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class Payment {

    private String id;
    private String organizationId;
    private RecordStatus recordStatus;
    private LocalDateTime createdAt;
    private String createdBy;
    private LocalDateTime updatedAt;
    private String updatedBy;

    private String userId;
    private LocalDateTime paymentDate;
    private Double totalAmount;
    private PaymentMethod paymentMethod;
    private PaymentStatus paymentStatus;
    private String receiptNumber;
    private String notes;

    private List<PaymentDetail> details;

    public boolean isCompleted() {
        return PaymentStatus.COMPLETED.equals(paymentStatus);
    }

    public boolean isPending() {
        return PaymentStatus.PENDING.equals(paymentStatus);
    }

    public boolean isCancelled() {
        return PaymentStatus.CANCELLED.equals(paymentStatus);
    }

    public boolean isActive() {
        return RecordStatus.ACTIVE.equals(recordStatus);
    }

    public Payment markAsCompleted(String updatedBy) {
        return this.toBuilder()
            .paymentStatus(PaymentStatus.COMPLETED)
            .updatedAt(LocalDateTime.now())
            .updatedBy(updatedBy)
            .build();
    }

    public Payment markAsCancelled(String updatedBy) {
        return this.toBuilder()
            .paymentStatus(PaymentStatus.CANCELLED)
            .updatedAt(LocalDateTime.now())
            .updatedBy(updatedBy)
            .build();
    }

    public Payment markAsDeleted(String deletedBy) {
        return this.toBuilder()
            .recordStatus(RecordStatus.INACTIVE)
            .paymentStatus(PaymentStatus.CANCELLED)
            .updatedAt(LocalDateTime.now())
            .updatedBy(deletedBy)
            .build();
    }

    public Payment markAsActive(String restoredBy) {
        return this.toBuilder()
            .recordStatus(RecordStatus.ACTIVE)
            .paymentStatus(PaymentStatus.COMPLETED)
            .updatedAt(LocalDateTime.now())
            .updatedBy(restoredBy)
            .build();
    }
}