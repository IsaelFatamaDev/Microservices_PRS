package pe.edu.vallegrande.vgmscommercial.application.dto.request.debt;

import jakarta.validation.constraints.DecimalMin;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateDebtRequest {

     @DecimalMin(value = "0.0", message = "Pending amount cannot be negative")
     private Double pendingAmount;

     @DecimalMin(value = "0.0", message = "Late fee cannot be negative")
     private Double lateFee;

     private String debtStatus;
}
