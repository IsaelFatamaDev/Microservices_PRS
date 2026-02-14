package pe.edu.vallegrande.vgmswaterquality.application.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateQualityTestRequest {
    private String organizationId;
    private String testingPointId;
    private String testType;
    private LocalDateTime testDate;
    private Double chlorineLevel;
    private Double phLevel;
    private Double turbidityLevel;
    private String testResult;
    private String testedByUserId;
    private String observations;
    private Boolean treatmentApplied;
    private String treatmentDescription;
}
