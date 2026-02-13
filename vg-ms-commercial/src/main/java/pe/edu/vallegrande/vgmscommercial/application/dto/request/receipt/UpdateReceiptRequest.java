package pe.edu.vallegrande.vgmscommercial.application.dto.request.receipt;

import jakarta.validation.constraints.DecimalMin;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateReceiptRequest {

     @DecimalMin(value = "0.0", message = "Paid amount cannot be negative")
     private Double paidAmount;

     private String receiptStatus;

     private String notes;
}
