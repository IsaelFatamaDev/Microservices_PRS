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
public class DebtResponse {

     private String id;
     private String organizationId;
     private String recordStatus;
     private LocalDateTime createdAt;
     private String createdBy;
     private LocalDateTime updatedAt;
     private String updatedBy;
     private String userId;
     private String userFullName;
     private Integer periodMonth;
     private Integer periodYear;
     private String periodDescription;
     private Double originalAmount;
     private Double pendingAmount;
     private Double lateFee;
     private Double totalAmount;
     private String debtStatus;
     private LocalDateTime dueDate;
}
