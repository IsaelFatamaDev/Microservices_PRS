package pe.edu.vallegrande.vgmswaterquality.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class CreateQualityTestRequest {
    @NotBlank(message = "ID de organización es obligatorio")
    private String organizationId;

    @NotBlank(message = "ID de punto de prueba es obligatorio")
    private String testingPointId;

    @NotBlank(message = "Tipo de prueba es obligatorio")
    private String testType;

    private LocalDateTime testDate;
    private Double chlorineLevel;
    private Double phLevel;
    private Double turbidityLevel;

    @NotBlank(message = "Resultado de prueba es obligatorio")
    private String testResult;

    private String testedByUserId;
    private String observations;
    private Boolean treatmentApplied;
    private String treatmentDescription;
}
