package pe.edu.vallegrande.vgmscommercial.application.dto.request.receipt;

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
public class CreateReceiptRequest {

     @NotBlank(message = "User ID is required")
     private String userId;

     @NotNull(message = "Period month is required")
     @Min(value = 1, message = "Month must be between 1 and 12")
     @Max(value = 12, message = "Month must be between 1 and 12")
     private Integer periodMonth;

     @NotNull(message = "Period year is required")
     @Min(value = 2020, message = "Year must be 2020 or later")
     private Integer periodYear;

     @NotNull(message = "Total amount is required")
     @DecimalMin(value = "0.01", message = "Total amount must be greater than 0")
     private Double totalAmount;

     private Double paidAmount;

     private String receiptStatus;

     private String notes;

     @Valid
     @NotEmpty(message = "At least one detail is required")
     private List<CreateReceiptDetailRequest> details;
}
