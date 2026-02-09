package pe.edu.vallegrande.vgmsorganizations.application.dto.request;

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
public class UpdateStreetRequest {

    @Pattern(regexp = "^(JR|AV|CALLE|PASAJE)$", message = "El tipo de calle debe ser JR, AV, CALLE o PASAJE")
    private String streetType;

    @Size(min = 2, max = 150, message = "El nombre debe tener entre 2 y 150 caracteres")
    private String streetName;
}
