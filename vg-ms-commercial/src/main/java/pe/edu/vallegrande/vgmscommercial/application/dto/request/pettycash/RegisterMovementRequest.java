package pe.edu.vallegrande.vgmscommercial.application.dto.request.pettycash;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterMovementRequest {

     @NotBlank(message = "Petty cash ID is required")
     private String pettyCashId;

     @NotBlank(message = "Movement type is required")
     private String movementType;

     @NotNull(message = "Amount is required")
     @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
     private Double amount;

     @NotBlank(message = "Category is required")
     private String category;

     @NotBlank(message = "Description is required")
     private String description;

     private String voucherNumber;
}
