package pe.edu.vallegrande.vgmsinventorypurchases.domain.models;

import lombok.Builder;
import lombok.Getter;
import pe.edu.vallegrande.vgmsinventorypurchases.domain.models.valueobjects.RecordStatus;

import java.time.LocalDateTime;

@Getter
@Builder(toBuilder = true)
public class Supplier {
    private String id;
    private String organizationId;
    private RecordStatus recordStatus;
    private LocalDateTime createdAt;
    private String createdBy;
    private LocalDateTime updatedAt;
    private String updatedBy;
    private String supplierName;
    private String ruc;
    private String address;
    private String phone;
    private String email;
    private String contactPerson;

    public boolean isActiveRecord() {
        return recordStatus == RecordStatus.ACTIVE;
    }

    public Supplier markAsDeleted(String deletedBy) {
        return this.toBuilder()
                .recordStatus(RecordStatus.INACTIVE)
                .updatedAt(LocalDateTime.now())
                .updatedBy(deletedBy)
                .build();
    }

    public Supplier restore(String restoredBy) {
        return this.toBuilder()
                .recordStatus(RecordStatus.ACTIVE)
                .updatedAt(LocalDateTime.now())
                .updatedBy(restoredBy)
                .build();
    }

    public Supplier updateWith(Supplier changes, String updatedBy) {
        var builder = this.toBuilder()
                .updatedAt(LocalDateTime.now())
                .updatedBy(updatedBy);
        if (changes.getSupplierName() != null)
            builder.supplierName(changes.getSupplierName());
        if (changes.getRuc() != null)
            builder.ruc(changes.getRuc());
        if (changes.getAddress() != null)
            builder.address(changes.getAddress());
        if (changes.getPhone() != null)
            builder.phone(changes.getPhone());
        if (changes.getEmail() != null)
            builder.email(changes.getEmail());
        if (changes.getContactPerson() != null)
            builder.contactPerson(changes.getContactPerson());
        return builder.build();
    }
}
