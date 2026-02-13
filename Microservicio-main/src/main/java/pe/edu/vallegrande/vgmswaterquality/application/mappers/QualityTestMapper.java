package pe.edu.vallegrande.vgmswaterquality.application.mappers;

import org.springframework.stereotype.Component;
import pe.edu.vallegrande.vgmswaterquality.application.dto.request.CreateQualityTestRequest;
import pe.edu.vallegrande.vgmswaterquality.application.dto.request.UpdateQualityTestRequest;
import pe.edu.vallegrande.vgmswaterquality.application.dto.response.QualityTestResponse;
import pe.edu.vallegrande.vgmswaterquality.domain.models.QualityTest;
import pe.edu.vallegrande.vgmswaterquality.infrastructure.persistence.documents.QualityTestDocument;

@Component
public class QualityTestMapper {

    public QualityTest toDomain(CreateQualityTestRequest request) {
        return QualityTest.builder()
                .organizationId(request.getOrganizationId())
                .testingPointId(request.getTestingPointId())
                .testDate(request.getTestDate())
                .testType(request.getTestType())
                .chlorineLevel(request.getChlorineLevel())
                .phLevel(request.getPhLevel())
                .turbidityLevel(request.getTurbidityLevel())
                .testResult(request.getTestResult())
                .observations(request.getObservations())
                .treatmentApplied(request.getTreatmentApplied())
                .treatmentDescription(request.getTreatmentDescription())
                .testedByUserId(request.getTestedByUserId())
                .build();
    }

    public QualityTest toDomain(UpdateQualityTestRequest request) {
        return QualityTest.builder()
                .organizationId(request.getOrganizationId())
                .testingPointId(request.getTestingPointId())
                .testDate(request.getTestDate())
                .testType(request.getTestType())
                .chlorineLevel(request.getChlorineLevel())
                .phLevel(request.getPhLevel())
                .turbidityLevel(request.getTurbidityLevel())
                .testResult(request.getTestResult())
                .observations(request.getObservations())
                .treatmentApplied(request.getTreatmentApplied())
                .treatmentDescription(request.getTreatmentDescription())
                .testedByUserId(request.getTestedByUserId())
                .build();
    }

    public QualityTestResponse toResponse(QualityTest test) {
        return QualityTestResponse.builder()
                .id(test.getId())
                .organizationId(test.getOrganizationId())
                .recordStatus(test.getRecordStatus())
                .testingPointId(test.getTestingPointId())
                .testDate(test.getTestDate())
                .testType(test.getTestType())
                .chlorineLevel(test.getChlorineLevel())
                .phLevel(test.getPhLevel())
                .turbidityLevel(test.getTurbidityLevel())
                .testResult(test.getTestResult())
                .testedByUserId(test.getTestedByUserId())
                .observations(test.getObservations())
                .treatmentApplied(test.getTreatmentApplied())
                .treatmentDescription(test.getTreatmentDescription())
                .createdAt(test.getCreatedAt())
                .createdBy(test.getCreatedBy())
                .updatedAt(test.getUpdatedAt())
                .updatedBy(test.getUpdatedBy())
                .build();
    }

    public QualityTestDocument toDocument(QualityTest test) {
        return QualityTestDocument.builder()
                .id(test.getId())
                .organizationId(test.getOrganizationId())
                .recordStatus(test.getRecordStatus())
                .testingPointId(test.getTestingPointId())
                .testDate(test.getTestDate())
                .testType(test.getTestType())
                .chlorineLevel(test.getChlorineLevel())
                .phLevel(test.getPhLevel())
                .turbidityLevel(test.getTurbidityLevel())
                .testResult(test.getTestResult())
                .testedByUserId(test.getTestedByUserId())
                .observations(test.getObservations())
                .treatmentApplied(test.getTreatmentApplied())
                .treatmentDescription(test.getTreatmentDescription())
                .createdAt(test.getCreatedAt())
                .createdBy(test.getCreatedBy())
                .updatedAt(test.getUpdatedAt())
                .updatedBy(test.getUpdatedBy())
                .build();
    }

    public QualityTest toDomain(QualityTestDocument doc) {
        return QualityTest.builder()
                .id(doc.getId())
                .organizationId(doc.getOrganizationId())
                .recordStatus(doc.getRecordStatus())
                .testingPointId(doc.getTestingPointId())
                .testDate(doc.getTestDate())
                .testType(doc.getTestType())
                .chlorineLevel(doc.getChlorineLevel())
                .phLevel(doc.getPhLevel())
                .turbidityLevel(doc.getTurbidityLevel())
                .testResult(doc.getTestResult())
                .testedByUserId(doc.getTestedByUserId())
                .observations(doc.getObservations())
                .treatmentApplied(doc.getTreatmentApplied())
                .treatmentDescription(doc.getTreatmentDescription())
                .createdAt(doc.getCreatedAt())
                .createdBy(doc.getCreatedBy())
                .updatedAt(doc.getUpdatedAt())
                .updatedBy(doc.getUpdatedBy())
                .build();
    }
}
