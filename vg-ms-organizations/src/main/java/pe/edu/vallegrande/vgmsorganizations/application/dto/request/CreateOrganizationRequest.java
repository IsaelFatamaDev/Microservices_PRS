package pe.edu.vallegrande.vgmsorganizations.application.dto.organization;

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

    @NotBlank(message = "Organization name is required")
    @Size(min = 3, max = 200, message = "Name must be between 3 and 200 characters")
    private String organizationName;

    @NotBlank(message = "District is required")
    @Size(max = 100)
    private String district;

    @NotBlank(message = "Province is required")
    @Size(max = 100)
    private String province;

    @NotBlank(message = "Department is required")
    @Size(max = 100)
    private String department;

    @Size(max = 250, message = "Address cannot exceed 250 characters")
    private String address;

    @Pattern(regexp = "^[+]?[0-9]{9,15}$", message = "Phone must be valid")
    private String phone;

    @Email(message = "Email must be valid")
    private String email;
}
