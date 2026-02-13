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
@Table("payment_details")
public class PaymentDetailEntity implements Persistable<String> {

     @Id
     private String id;

     @Column("payment_id")
     private String paymentId;

     @Column("payment_type")
     private String paymentType;

     @Column("description")
     private String description;

     @Column("amount")
     private Double amount;

     @Column("period_month")
     private Integer periodMonth;

     @Column("period_year")
     private Integer periodYear;

     @Column("created_at")
     private LocalDateTime createdAt;

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
