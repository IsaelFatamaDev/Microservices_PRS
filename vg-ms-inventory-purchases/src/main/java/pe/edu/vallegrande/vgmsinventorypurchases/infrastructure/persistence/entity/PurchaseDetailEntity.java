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
@Table("purchase_details")
public class PurchaseDetailEntity implements Persistable<String> {
    @Id
    private String id;
    @Column("purchase_id")
    private String purchaseId;
    @Column("material_id")
    private String materialId;
    @Column("quantity")
    private Double quantity;
    @Column("unit_price")
    private Double unitPrice;
    @Column("subtotal")
    private Double subtotal;
    @Column("created_at")
    private LocalDateTime createdAt;

    @Transient
    @Builder.Default
    private boolean isNew = true;

    @Override
    public boolean isNew() {
        return isNew || id == null;
    }
}
