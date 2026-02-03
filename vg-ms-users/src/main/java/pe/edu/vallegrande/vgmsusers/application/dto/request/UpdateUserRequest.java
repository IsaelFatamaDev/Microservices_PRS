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
public class UpdateUserRequest {

    @Size(min = 2, max = 100, message = "El nombre debe tener entre 2 y 100 caracteres")
    private String firstName;

    @Size(min = 2, max = 100, message = "El apellido debe tener entre 2 y 100 caracteres")
    private String lastName;

    @Email(message = "El email debe de ser válido")
    private String email;

    @Pattern(regexp = "^9\\d{8}$", message = "El teléfono debe de ser válido (opcional)")
    private String phone;

    @Size(max = 250, message = "La dirección no puede exceder de los 250 carácteres")
    private String address;
}
