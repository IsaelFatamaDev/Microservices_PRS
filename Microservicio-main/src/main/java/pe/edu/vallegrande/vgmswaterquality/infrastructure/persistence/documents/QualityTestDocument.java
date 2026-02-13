package pe.edu.vallegrande.vgmswaterquality.infrastructure.persistence.documents;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "quality_tests")
public class QualityTestDocument {

    @Id
    private String id;

    @Field("organization_id")
    private String organizationId;

    @Field("record_status")
    private String recordStatus;

    @Field("created_at")
    private LocalDateTime createdAt;

    @Field("created_by")
    private String createdBy;

    @Field("updated_at")
    private LocalDateTime updatedAt;

    @Field("updated_by")
    private String updatedBy;

    @Field("testing_point_id")
    private String testingPointId;

    @Field("test_date")
    private LocalDateTime testDate;

    @Field("test_type")
    private String testType;

    @Field("chlorine_level")
    private Double chlorineLevel;

    @Field("ph_level")
    private Double phLevel;

    @Field("turbidity_level")
    private Double turbidityLevel;

    @Field("test_result")
    private String testResult;

    @Field("tested_by_user_id")
    private String testedByUserId;

    @Field("observations")
    private String observations;

    @Field("treatment_applied")
    private Boolean treatmentApplied;

    @Field("treatment_description")
    private String treatmentDescription;
}
