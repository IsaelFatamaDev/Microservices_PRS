package pe.edu.vallegrande.vgmscommercial.application.dto.request.payment;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreatePaymentDetailRequest {

     @NotBlank(message = "Payment type is required")
     private String paymentType;

     @NotBlank(message = "Description is required")
     private String description;

     @NotNull(message = "Amount is required")
     @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
     private Double amount;

     @Min(value = 1, message = "Month must be between 1 and 12")
     @Max(value = 12, message = "Month must be between 1 and 12")
     private Integer periodMonth;

     @Min(value = 2020, message = "Year must be 2020 or later")
     private Integer periodYear;
}
