package pe.edu.vallegrande.vgmsorganizations.application.dto.organization;

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

    @Size(min = 3, max = 200, message = "Name must be between 3 and 200 characters")
    private String organizationName;

    @Size(max = 100)
    private String district;

    @Size(max = 100)
    private String province;

    @Size(max = 100)
    private String department;

    @Size(max = 250)
    private String address;

    @Pattern(regexp = "^[+]?[0-9]{9,15}$", message = "Phone must be valid")
    private String phone;

    @Email(message = "Email must be valid")
    private String email;
}