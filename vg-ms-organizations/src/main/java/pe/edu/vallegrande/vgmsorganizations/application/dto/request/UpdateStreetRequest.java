package pe.edu.vallegrande.vgmsorganizations.application.dto.street;

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

    @Pattern(regexp = "^(JR|AV|CALLE|PASAJE)$", message = "Street type must be JR, AV, CALLE, or PASAJE")
    private String streetType;

    @Size(min = 2, max = 150, message = "Name must be between 2 and 150 characters")
    private String streetName;
}