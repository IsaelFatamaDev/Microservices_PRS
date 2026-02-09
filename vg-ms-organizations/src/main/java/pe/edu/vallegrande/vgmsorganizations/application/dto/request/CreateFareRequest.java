package pe.edu.vallegrande.vgmsorganizations.application.dto.fare;

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
public class CreateFareRequest {

    @NotBlank(message = "Organization ID is required")
    private String organizationId;

    @NotBlank(message = "Fare type is required")
    @Pattern(regexp = "^[A-Z0-9_]{2,50}$", message = "Invalid fare type format")
    private String fareType;

    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be greater than 0")
    private Double amount;

    @Size(max = 250)
    private String description;

    private LocalDateTime validFrom;
    private LocalDateTime validTo;
}