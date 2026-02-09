package pe.edu.vallegrande.vgmsusers.application.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateUserRequest {

    private String organizationId;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(min = 2, max = 100, message = "El nombre debe tener entre 2 y 100 caracteres")
    private String firstName;

    @NotBlank(message = "El apellido es obligatorio")
    @Size(min = 2, max = 100, message = "El apellido debe tener entre 2 y 100 caracteres")
    private String lastName;

    @NotBlank(message = "El tipo de documento es obligatorio")
    @Pattern(regexp = "^(DNI|CNE)$", message = "El tipo de documento debe ser DNI o CNE")
    private String documentType;

    @NotBlank(message = "El número de documento es obligatorio")
    @Pattern(regexp = "^[A-Za-z0-9]{8,20}$", message = "Documento inválido")
    private String documentNumber;

    @Email(message = "El email debe ser válido")
    private String email;

    @Pattern(regexp = "^9\\d{8}$", message = "El teléfono debe de ser válido (opcional)")
    private String phone;

    @Size(max = 250, message = "La dirección no puede exceder de los 250 carácteres")
    private String address;

    private String zoneId;

    private String streetId;

    @NotBlank(message = "El rol es obligatorio")
    @Pattern(regexp = "^(SUPER_ADMIN|CLIENT|OPERATOR|ADMIN)$", message = "El rol debe ser SUPER_ADMIN, ADMIN, CLIENT o OPERATOR")
    private String role;
}
