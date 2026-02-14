package pe.edu.vallegrande.vgmswaterquality.application.mappers;

import org.springframework.stereotype.Component;
import pe.edu.vallegrande.vgmswaterquality.application.dto.request.CreateTestingPointRequest;
import pe.edu.vallegrande.vgmswaterquality.application.dto.request.UpdateTestingPointRequest;
import pe.edu.vallegrande.vgmswaterquality.application.dto.response.TestingPointResponse;
import pe.edu.vallegrande.vgmswaterquality.domain.models.TestingPoint;
import pe.edu.vallegrande.vgmswaterquality.infrastructure.persistence.documents.TestingPointDocument;

@Component
public class TestingPointMapper {

    public TestingPoint toDomain(CreateTestingPointRequest request) {
        return TestingPoint.builder()
                .organizationId(request.getOrganizationId())
                .zoneId(request.getZoneId())
                .pointName(request.getPointName())
                .pointType(request.getPointType())
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .location(request.getLocation())
                .build();
    }

    public TestingPoint toDomain(UpdateTestingPointRequest request) {
        return TestingPoint.builder()
                .organizationId(request.getOrganizationId())
                .zoneId(request.getZoneId())
                .pointName(request.getPointName())
                .pointType(request.getPointType())
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .location(request.getLocation())
                .build();
    }

    public TestingPointResponse toResponse(TestingPoint point) {
        return TestingPointResponse.builder()
                .id(point.getId())
                .organizationId(point.getOrganizationId())
                .zoneId(point.getZoneId())
                .pointName(point.getPointName())
                .pointType(point.getPointType())
                .location(point.getLocation())
                .latitude(point.getLatitude())
                .longitude(point.getLongitude())
                .recordStatus(point.getRecordStatus())
                .createdAt(point.getCreatedAt())
                .updatedAt(point.getUpdatedAt())
                .createdBy(point.getCreatedBy())
                .updatedBy(point.getUpdatedBy())
                .build();
    }

    public TestingPointDocument toDocument(TestingPoint point) {
        return TestingPointDocument.builder()
                .id(point.getId())
                .organizationId(point.getOrganizationId())
                .zoneId(point.getZoneId())
                .pointName(point.getPointName())
                .pointType(point.getPointType())
                .location(point.getLocation())
                .latitude(point.getLatitude() != null ? Double.valueOf(point.getLatitude()) : null)
                .longitude(point.getLongitude() != null ? Double.valueOf(point.getLongitude()) : null)
                .recordStatus(point.getRecordStatus())
                .createdAt(point.getCreatedAt())
                .updatedAt(point.getUpdatedAt())
                .createdBy(point.getCreatedBy())
                .updatedBy(point.getUpdatedBy())
                .build();
    }

    public TestingPoint toDomain(TestingPointDocument doc) {
        return TestingPoint.builder()
                .id(doc.getId())
                .organizationId(doc.getOrganizationId())
                .zoneId(doc.getZoneId())
                .pointName(doc.getPointName())
                .pointType(doc.getPointType())
                .location(doc.getLocation())
                .latitude(doc.getLatitude() != null ? String.valueOf(doc.getLatitude()) : null)
                .longitude(doc.getLongitude() != null ? String.valueOf(doc.getLongitude()) : null)
                .recordStatus(doc.getRecordStatus())
                .createdAt(doc.getCreatedAt())
                .updatedAt(doc.getUpdatedAt())
                .createdBy(doc.getCreatedBy())
                .updatedBy(doc.getUpdatedBy())
                .build();
    }
}
