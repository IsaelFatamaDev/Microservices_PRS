package pe.edu.vallegrande.vgmscommercial.domain.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import pe.edu.vallegrande.vgmscommercial.domain.models.valueobjects.CutReason;
import pe.edu.vallegrande.vgmscommercial.domain.models.valueobjects.CutStatus;
import pe.edu.vallegrande.vgmscommercial.domain.models.valueobjects.RecordStatus;

import java.time.LocalDateTime;

@Getter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class ServiceCut {

    private String id;
    private String organizationId;
    private RecordStatus recordStatus;
    private LocalDateTime createdAt;
    private String createdBy;
    private LocalDateTime updatedAt;
    private String updatedBy;

    private String userId;
    private String waterBoxId;
    private LocalDateTime scheduledDate;
    private LocalDateTime executedDate;
    private CutReason cutReason;
    private Double debtAmount;
    private LocalDateTime reconnectionDate;
    private Boolean reconnectionFeePaid;
    private CutStatus cutStatus;
    private String notes;

    public boolean isPending() {
        return CutStatus.PENDING.equals(cutStatus);
    }

    public boolean isExecuted() {
        return CutStatus.EXECUTED.equals(cutStatus);
    }

    public boolean isReconnected() {
        return CutStatus.RECONNECTED.equals(cutStatus);
    }

    public boolean isActive() {
        return RecordStatus.ACTIVE.equals(recordStatus);
    }

    public boolean isDueToNonPayment() {
        return CutReason.NON_PAYMENT.equals(cutReason);
    }

    public ServiceCut execute(String executedBy) {
        return this.toBuilder()
            .cutStatus(CutStatus.EXECUTED)
            .executedDate(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .updatedBy(executedBy)
            .build();
    }

    public ServiceCut reconnect(String reconnectedBy) {
        return this.toBuilder()
            .cutStatus(CutStatus.RECONNECTED)
            .reconnectionDate(LocalDateTime.now())
            .reconnectionFeePaid(true)
            .updatedAt(LocalDateTime.now())
            .updatedBy(reconnectedBy)
            .build();
    }

    public ServiceCut cancel(String cancelledBy) {
        return this.toBuilder()
            .cutStatus(CutStatus.CANCELLED)
            .updatedAt(LocalDateTime.now())
            .updatedBy(cancelledBy)
            .build();
    }

    public ServiceCut markAsDeleted(String deletedBy) {
        return this.toBuilder()
            .recordStatus(RecordStatus.INACTIVE)
            .cutStatus(CutStatus.CANCELLED)
            .updatedAt(LocalDateTime.now())
            .updatedBy(deletedBy)
            .build();
    }

    public ServiceCut markAsActive(String restoredBy) {
        return this.toBuilder()
            .recordStatus(RecordStatus.ACTIVE)
            .cutStatus(CutStatus.PENDING)
            .updatedAt(LocalDateTime.now())
            .updatedBy(restoredBy)
            .build();
    }
}