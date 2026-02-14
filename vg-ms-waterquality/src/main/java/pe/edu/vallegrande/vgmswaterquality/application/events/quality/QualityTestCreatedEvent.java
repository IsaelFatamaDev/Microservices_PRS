package pe.edu.vallegrande.vgmswaterquality.application.events.quality;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class QualityTestCreatedEvent {
    @Builder.Default
    private String eventType = "QUALITYTEST_CREATED";
    private String eventId;
    private LocalDateTime timestamp;
    private String organizationId;
    private String recordStatus;
    private String updatedBy;
    private String testingPointId;
    private LocalDateTime testDate;
    private String testType;
    private Double chlorineLevel;
    private Double phLevel;
    private Double turbidityLevel;
    private String testResult;
    private String observations;
    private Boolean treatmentApplied;
    private String treatmentDescription;

}
