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
public class PaymentDetailResponse {

     private String id;
     private String paymentId;
     private String paymentType;
     private String description;
     private Double amount;
     private Integer periodMonth;
     private Integer periodYear;
     private String periodDescription;
     private LocalDateTime createdAt;
}
