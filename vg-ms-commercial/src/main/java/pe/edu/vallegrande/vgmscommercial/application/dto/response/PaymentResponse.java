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
public class PaymentResponse {

     private String id;
     private String organizationId;
     private String recordStatus;
     private LocalDateTime createdAt;
     private String createdBy;
     private LocalDateTime updatedAt;
     private String updatedBy;
     private String userId;
     private String userFullName;
     private LocalDateTime paymentDate;
     private Double totalAmount;
     private String paymentMethod;
     private String paymentStatus;
     private String receiptNumber;
     private String notes;
     private List<PaymentDetailResponse> details;
}
