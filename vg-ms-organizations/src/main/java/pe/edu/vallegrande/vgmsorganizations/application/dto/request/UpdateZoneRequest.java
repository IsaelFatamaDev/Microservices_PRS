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
public class UpdateZoneRequest {

    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    private String zoneName;

    @Size(max = 250)
    private String description;
}
