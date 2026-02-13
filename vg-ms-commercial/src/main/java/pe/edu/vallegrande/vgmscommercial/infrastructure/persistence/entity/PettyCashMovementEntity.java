package pe.edu.vallegrande.vgmscommercial.infrastructure.persistence.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("petty_cash_movements")
public class PettyCashMovementEntity implements Persistable<String> {

     @Id
     private String id;

     @Column("organization_id")
     private String organizationId;

     @Column("created_at")
     private LocalDateTime createdAt;

     @Column("created_by")
     private String createdBy;

     @Column("petty_cash_id")
     private String pettyCashId;

     @Column("movement_date")
     private LocalDateTime movementDate;

     @Column("movement_type")
     private String movementType;

     @Column("amount")
     private Double amount;

     @Column("category")
     private String category;

     @Column("description")
     private String description;

     @Column("voucher_number")
     private String voucherNumber;

     @Column("previous_balance")
     private Double previousBalance;

     @Column("new_balance")
     private Double newBalance;

     @Transient
     private boolean newEntity;

     @Override
     public boolean isNew() {
          return newEntity;
     }

     public void setNotNew() {
          this.newEntity = false;
     }
}
