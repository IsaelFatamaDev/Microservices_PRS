package pe.edu.vallegrande.vgmsorganizations.application.dto.request;

import jakarta.validation.constraints.Email;
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
public class UpdateOrganizationRequest {

    @Size(min = 3, max = 200, message = "El nombre debe tener entre 3 y 200 caracteres")
    private String organizationName;

    @Size(max = 100, message = "El distrito no puede exceder 100 caracteres")
    private String district;

    @Size(max = 100, message = "La provincia no puede exceder 100 caracteres")
    private String province;

    @Size(max = 100, message = "El departamento no puede exceder 100 caracteres")
    private String department;

    @Size(max = 250, message = "La dirección no puede exceder 250 caracteres")
    private String address;

    @Pattern(regexp = "^[+]?[0-9]{9,15}$", message = "El teléfono debe ser válido")
    private String phone;

    @Email(message = "El email debe ser válido")
    private String email;
}
