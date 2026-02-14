package pe.edu.vallegrande.vgmsinventorypurchases.infrastructure.persistence.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Table("suppliers")
public class SupplierEntity implements Persistable<String> {
    @Id
    private String id;
    @Column("organization_id")
    private String organizationId;
    @Column("record_status")
    private String recordStatus;
    @Column("created_at")
    private LocalDateTime createdAt;
    @Column("created_by")
    private String createdBy;
    @Column("updated_at")
    private LocalDateTime updatedAt;
    @Column("updated_by")
    private String updatedBy;
    @Column("supplier_name")
    private String supplierName;
    @Column("ruc")
    private String ruc;
    @Column("address")
    private String address;
    @Column("phone")
    private String phone;
    @Column("email")
    private String email;
    @Column("contact_person")
    private String contactPerson;

    @Transient
    @Builder.Default
    private boolean isNew = true;

    @Override
    public boolean isNew() {
        return isNew || id == null;
    }
}
