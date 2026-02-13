package pe.edu.vallegrande.vgmsinventorypurchases.domain.models;

import lombok.Builder;
import lombok.Getter;
import pe.edu.vallegrande.vgmsinventorypurchases.domain.models.valueobjects.PurchaseStatus;
import pe.edu.vallegrande.vgmsinventorypurchases.domain.models.valueobjects.RecordStatus;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder(toBuilder = true)
public class Purchase {
    private String id;
    private String organizationId;
    private RecordStatus recordStatus;
    private LocalDateTime createdAt;
    private String createdBy;
    private LocalDateTime updatedAt;
    private String updatedBy;
    private String purchaseCode;
    private String supplierId;
    private LocalDateTime purchaseDate;
    private Double totalAmount;
    private PurchaseStatus purchaseStatus;
    private String invoiceNumber;
    private List<PurchaseDetail> details;

    public boolean isActiveRecord() {
        return recordStatus == RecordStatus.ACTIVE;
    }

    public Purchase markAsDeleted(String deletedBy) {
        return this.toBuilder()
                .recordStatus(RecordStatus.INACTIVE)
                .updatedAt(LocalDateTime.now())
                .updatedBy(deletedBy)
                .build();
    }

    public Purchase restore(String restoredBy) {
        return this.toBuilder()
                .recordStatus(RecordStatus.ACTIVE)
                .updatedAt(LocalDateTime.now())
                .updatedBy(restoredBy)
                .build();
    }

    public Purchase receive(String receivedBy) {
        return this.toBuilder()
                .purchaseStatus(PurchaseStatus.RECEIVED)
                .updatedAt(LocalDateTime.now())
                .updatedBy(receivedBy)
                .build();
    }

    public Purchase cancel(String cancelledBy) {
        return this.toBuilder()
                .purchaseStatus(PurchaseStatus.CANCELLED)
                .updatedAt(LocalDateTime.now())
                .updatedBy(cancelledBy)
                .build();
    }

    public Purchase updateWith(Purchase changes, String updatedBy) {
        var builder = this.toBuilder()
                .updatedAt(LocalDateTime.now())
                .updatedBy(updatedBy);
        if (changes.getSupplierId() != null)
            builder.supplierId(changes.getSupplierId());
        if (changes.getPurchaseDate() != null)
            builder.purchaseDate(changes.getPurchaseDate());
        if (changes.getTotalAmount() != null)
            builder.totalAmount(changes.getTotalAmount());
        if (changes.getInvoiceNumber() != null)
            builder.invoiceNumber(changes.getInvoiceNumber());
        return builder.build();
    }
}
