package pe.edu.vallegrande.vgmswaterquality.domain.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pe.edu.vallegrande.vgmswaterquality.domain.models.valueobjects.RecordStatus;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class QualityTest {
    private String id;
    private String organizationId;
    private String recordStatus;
    private LocalDateTime createdAt;
    private String createdBy;
    private LocalDateTime updatedAt;
    private String updatedBy;
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

    public boolean isActive() {
        return RecordStatus.ACTIVE.name().equals(recordStatus);
    }

    public boolean isInactive() {
        return RecordStatus.INACTIVE.name().equals(recordStatus);
    }
}
