package pe.edu.vallegrande.vgmsorganizations.application.dto.zone;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateZoneRequest {

    @NotBlank(message = "Organization ID is required")
    private String organizationId;

    @NotBlank(message = "Zone name is required")
    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    private String zoneName;

    @Size(max = 250)
    private String description;
}