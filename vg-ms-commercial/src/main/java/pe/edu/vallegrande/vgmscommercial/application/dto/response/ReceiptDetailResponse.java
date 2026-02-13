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
public class ReceiptDetailResponse {

     private String id;
     private String receiptId;
     private String conceptType;
     private String description;
     private Double amount;
     private LocalDateTime createdAt;
}
