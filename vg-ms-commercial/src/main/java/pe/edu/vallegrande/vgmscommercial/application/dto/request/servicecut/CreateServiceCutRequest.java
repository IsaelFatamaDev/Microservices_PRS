package pe.edu.vallegrande.vgmscommercial.application.dto.request.servicecut;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateServiceCutRequest {

     @NotBlank(message = "User ID is required")
     private String userId;

     private String waterBoxId;

     @NotNull(message = "Scheduled date is required")
     private LocalDateTime scheduledDate;

     @NotBlank(message = "Cut reason is required")
     private String cutReason;

     @DecimalMin(value = "0.0", message = "Debt amount cannot be negative")
     private Double debtAmount;

     private String notes;
}
