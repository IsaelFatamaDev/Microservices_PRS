package pe.edu.vallegrande.vgmsorganizations.application.dto.request;

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
public class CreateStreetRequest {

    @NotBlank(message = "El ID de la organización es obligatorio")
    private String organizationId;

    @NotBlank(message = "El ID de la zona es obligatorio")
    private String zoneId;

    @NotBlank(message = "El tipo de calle es obligatorio")
    @Pattern(regexp = "^(JR|AV|CALLE|PASAJE)$", message = "El tipo de calle debe ser JR, AV, CALLE o PASAJE")
    private String streetType;

    @NotBlank(message = "El nombre de la calle es obligatorio")
    @Size(min = 2, max = 150, message = "El nombre debe tener entre 2 y 150 caracteres")
    private String streetName;
}
