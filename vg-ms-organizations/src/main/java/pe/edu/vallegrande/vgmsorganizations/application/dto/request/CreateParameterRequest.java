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
public class CreateParameterRequest {

    @NotBlank(message = "El ID de la organización es obligatorio")
    private String organizationId;

    @NotBlank(message = "El tipo de parámetro es obligatorio")
    @Pattern(regexp = "^[A-Z0-9_]{2,50}$", message = "Formato de tipo de parámetro inválido")
    private String parameterType;

    @NotBlank(message = "El valor es obligatorio")
    private String parameterValue;

    @Size(max = 250, message = "La descripción no puede exceder 250 caracteres")
    private String description;
}
