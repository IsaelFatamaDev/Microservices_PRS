package pe.edu.vallegrande.vgmswaterquality.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class QualityTestResponse {
    private String id;
    private String organizationId;
    private String recordStatus;
    private String testingPointId;
    private LocalDateTime testDate;
    private String testType;
    private Double chlorineLevel;
    private Double phLevel;
    private Double turbidityLevel;
    private String testResult;
    private String testedByUserId;
    private String observations;
    private Boolean treatmentApplied;
    private String treatmentDescription;
    private LocalDateTime createdAt;
    private String createdBy;
    private LocalDateTime updatedAt;
    private String updatedBy;
}
