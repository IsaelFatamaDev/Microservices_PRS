package pe.edu.vallegrande.vgmscommercial.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PettyCashMovementResponse {

     private String id;
     private String organizationId;
     private LocalDateTime createdAt;
     private String createdBy;
     private String pettyCashId;
     private LocalDateTime movementDate;
     private String movementType;
     private Double amount;
     private String category;
     private String description;
     private String voucherNumber;
     private Double previousBalance;
     private Double newBalance;
}
