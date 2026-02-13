package pe.edu.vallegrande.vgmscommercial.application.dto.request.payment;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreatePaymentRequest {

     @NotBlank(message = "User ID is required")
     private String userId;

     @NotNull(message = "Total amount is required")
     @DecimalMin(value = "0.01", message = "Total amount must be greater than 0")
     private Double totalAmount;

     @NotBlank(message = "Payment method is required")
     private String paymentMethod;

     private String notes;

     @Valid
     @NotEmpty(message = "At least one detail is required")
     private List<CreatePaymentDetailRequest> details;
}
