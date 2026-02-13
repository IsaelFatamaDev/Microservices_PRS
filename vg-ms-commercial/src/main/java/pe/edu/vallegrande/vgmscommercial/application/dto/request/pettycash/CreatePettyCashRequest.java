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
public class CreatePettyCashRequest {

     @NotBlank(message = "Responsible user ID is required")
     private String responsibleUserId;

     @NotNull(message = "Initial balance is required")
     @DecimalMin(value = "0.01", message = "Initial balance must be greater than 0")
     private Double initialBalance;

     @NotNull(message = "Max amount limit is required")
     @DecimalMin(value = "0.01", message = "Max amount limit must be greater than 0")
     private Double maxAmountLimit;
}
