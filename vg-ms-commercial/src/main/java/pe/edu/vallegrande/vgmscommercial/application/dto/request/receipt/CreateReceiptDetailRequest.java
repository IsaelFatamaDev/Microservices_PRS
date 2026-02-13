package pe.edu.vallegrande.vgmscommercial.application.dto.request.receipt;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateReceiptDetailRequest {

     @NotBlank(message = "Concept type is required")
     private String conceptType;

     @NotBlank(message = "Description is required")
     private String description;

     @NotNull(message = "Amount is required")
     @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
     private Double amount;
}
