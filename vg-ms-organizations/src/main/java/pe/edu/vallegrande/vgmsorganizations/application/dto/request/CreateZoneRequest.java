package pe.edu.vallegrande.vgmsorganizations.application.dto.request;

import jakarta.validation.constraints.NotBlank;
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

    @NotBlank(message = "El ID de la organización es obligatorio")
    private String organizationId;

    @NotBlank(message = "El nombre de la zona es obligatorio")
    @Size(min = 2, max = 100, message = "El nombre debe tener entre 2 y 100 caracteres")
    private String zoneName;

    @Size(max = 250, message = "La descripción no puede exceder 250 caracteres")
    private String description;
}
