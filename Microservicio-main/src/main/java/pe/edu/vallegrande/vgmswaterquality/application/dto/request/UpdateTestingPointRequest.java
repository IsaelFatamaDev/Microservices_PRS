package pe.edu.vallegrande.vgmswaterquality.application.dto.request;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateTestingPointRequest {
    private String organizationId;
    private String zoneId;

    @Size(min = 2, max = 100, message = "Nombre debe tener entre 2 y 100 caracteres")
    private String pointName;

    private String pointType;
    private String location;
    private String latitude;
    private String longitude;
}
