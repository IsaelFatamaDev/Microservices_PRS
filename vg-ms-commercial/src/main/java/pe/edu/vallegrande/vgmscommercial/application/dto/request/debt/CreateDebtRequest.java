package pe.edu.vallegrande.vgmscommercial.application.dto.request.debt;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateDebtRequest {

     @NotBlank(message = "User ID is required")
     private String userId;

     @NotNull(message = "Period month is required")
     @Min(value = 1, message = "Month must be between 1 and 12")
     @Max(value = 12, message = "Month must be between 1 and 12")
     private Integer periodMonth;

     @NotNull(message = "Period year is required")
     @Min(value = 2020, message = "Year must be 2020 or later")
     private Integer periodYear;

     @NotNull(message = "Original amount is required")
     @DecimalMin(value = "0.01", message = "Original amount must be greater than 0")
     private Double originalAmount;
}
