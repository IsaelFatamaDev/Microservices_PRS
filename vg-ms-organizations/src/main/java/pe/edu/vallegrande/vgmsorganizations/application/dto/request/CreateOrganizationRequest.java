package pe.edu.vallegrande.vgmsorganizations.application.dto.request;

import jakarta.validation.constraints.Email;
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
public class CreateOrganizationRequest {

    @NotBlank(message = "El nombre de la organización es obligatorio")
    @Size(min = 3, max = 200, message = "El nombre debe tener entre 3 y 200 caracteres")
    private String organizationName;

    @NotBlank(message = "El distrito es obligatorio")
    @Size(max = 100, message = "El distrito no puede exceder 100 caracteres")
    private String district;

    @NotBlank(message = "La provincia es obligatoria")
    @Size(max = 100, message = "La provincia no puede exceder 100 caracteres")
    private String province;

    @NotBlank(message = "El departamento es obligatorio")
    @Size(max = 100, message = "El departamento no puede exceder 100 caracteres")
    private String department;

    @Size(max = 250, message = "La dirección no puede exceder 250 caracteres")
    private String address;

    @Pattern(regexp = "^[+]?[0-9]{9,15}$", message = "El teléfono debe ser válido")
    private String phone;

    @Email(message = "El email debe ser válido")
    private String email;

    private String logoUrl;
}
