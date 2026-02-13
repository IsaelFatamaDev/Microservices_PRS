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
@Table("debts")
public class DebtEntity implements Persistable<String> {

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

     @Column("user_id")
     private String userId;

     @Column("period_month")
     private Integer periodMonth;

     @Column("period_year")
     private Integer periodYear;

     @Column("original_amount")
     private Double originalAmount;

     @Column("pending_amount")
     private Double pendingAmount;

     @Column("late_fee")
     private Double lateFee;

     @Column("debt_status")
     private String debtStatus;

     @Column("due_date")
     private LocalDateTime dueDate;

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
