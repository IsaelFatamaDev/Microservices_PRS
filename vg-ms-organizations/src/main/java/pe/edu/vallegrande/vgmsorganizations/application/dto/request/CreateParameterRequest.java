package pe.edu.vallegrande.vgmsorganizations.application.dto.parameter;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateParameterRequest {

    @NotBlank(message = "Organization ID is required")
    private String organizationId;

    @NotBlank(message = "Parameter type is required")
    @Pattern(regexp = "^[A-Z0-9_]{2,50}$", message = "Invalid parameter type format")
    private String parameterType;

    @NotBlank(message = "Value is required")
    private String parameterValue;

    @Size(max = 250)
    private String description;
}