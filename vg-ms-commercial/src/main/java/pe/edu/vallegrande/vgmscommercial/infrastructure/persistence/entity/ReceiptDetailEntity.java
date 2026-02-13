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
@Table("receipt_details")
public class ReceiptDetailEntity implements Persistable<String> {

     @Id
     private String id;

     @Column("receipt_id")
     private String receiptId;

     @Column("concept_type")
     private String conceptType;

     @Column("description")
     private String description;

     @Column("amount")
     private Double amount;

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
