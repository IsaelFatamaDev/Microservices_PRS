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
@Table("petty_cash")
public class PettyCashEntity implements Persistable<String> {

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

     @Column("responsible_user_id")
     private String responsibleUserId;

     @Column("current_balance")
     private Double currentBalance;

     @Column("max_amount_limit")
     private Double maxAmountLimit;

     @Column("petty_cash_status")
     private String pettyCashStatus;

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
