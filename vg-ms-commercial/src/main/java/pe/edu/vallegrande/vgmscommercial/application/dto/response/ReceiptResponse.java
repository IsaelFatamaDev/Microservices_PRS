package pe.edu.vallegrande.vgmscommercial.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReceiptResponse {

     private String id;
     private String organizationId;
     private String recordStatus;
     private LocalDateTime createdAt;
     private String createdBy;
     private LocalDateTime updatedAt;
     private String updatedBy;
     private String receiptNumber;
     private String userId;
     private String userFullName;
     private Integer periodMonth;
     private Integer periodYear;
     private String periodDescription;
     private LocalDateTime issueDate;
     private LocalDateTime dueDate;
     private Double totalAmount;
     private Double paidAmount;
     private Double pendingAmount;
     private String receiptStatus;
     private String notes;
     private List<ReceiptDetailResponse> details;
}
