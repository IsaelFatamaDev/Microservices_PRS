package pe.edu.vallegrande.vgmsorganizations.application.dto.parameter;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateParameterRequest {

    private String parameterValue;

    @Size(max = 250)
    private String description;
}