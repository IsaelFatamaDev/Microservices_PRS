package pe.edu.vallegrande.vgmsorganizations.application.mappers;

import org.springframework.stereotype.Component;
import pe.edu.vallegrande.vgmsorganizations.application.dto.request.CreateStreetRequest;
import pe.edu.vallegrande.vgmsorganizations.application.dto.request.UpdateStreetRequest;
import pe.edu.vallegrande.vgmsorganizations.application.dto.response.StreetResponse;
import pe.edu.vallegrande.vgmsorganizations.domain.models.Street;
import pe.edu.vallegrande.vgmsorganizations.domain.models.valueobjects.RecordStatus;
import pe.edu.vallegrande.vgmsorganizations.domain.models.valueobjects.StreetType;
import pe.edu.vallegrande.vgmsorganizations.infrastructure.persistence.documents.StreetDocument;

@Component
public class StreetMapper {

    public Street toModel(CreateStreetRequest request) {
        return Street.builder()
            .organizationId(request.getOrganizationId())
            .zoneId(request.getZoneId())
            .streetType(StreetType.valueOf(request.getStreetType()))
            .streetName(request.getStreetName())
            .build();
    }

    public Street toModel(UpdateStreetRequest request) {
        return Street.builder()
            .streetType(request.getStreetType() != null ? StreetType.valueOf(request.getStreetType()) : null)
            .streetName(request.getStreetName())
            .build();
    }

    public StreetResponse toResponse(Street street) {
        return StreetResponse.builder()
            .id(street.getId())
            .organizationId(street.getOrganizationId())
            .zoneId(street.getZoneId())
            .streetType(street.getStreetType().name())
            .streetTypeDisplayName(street.getStreetType().getDisplayName())
            .streetName(street.getStreetName())
            .fullStreetName(street.getFullStreetName())
            .recordStatus(street.getRecordStatus().name())
            .createdAt(street.getCreatedAt())
            .createdBy(street.getCreatedBy())
            .updatedAt(street.getUpdatedAt())
            .updatedBy(street.getUpdatedBy())
            .build();
    }

    public StreetDocument toDocument(Street street) {
        return StreetDocument.builder()
            .id(street.getId())
            .organizationId(street.getOrganizationId())
            .zoneId(street.getZoneId())
            .streetType(street.getStreetType().name())
            .streetName(street.getStreetName())
            .recordStatus(street.getRecordStatus().name())
            .createdAt(street.getCreatedAt())
            .createdBy(street.getCreatedBy())
            .updatedAt(street.getUpdatedAt())
            .updatedBy(street.getUpdatedBy())
            .build();
    }

    public Street toModel(StreetDocument doc) {
        return Street.builder()
            .id(doc.getId())
            .organizationId(doc.getOrganizationId())
            .zoneId(doc.getZoneId())
            .streetType(StreetType.valueOf(doc.getStreetType()))
            .streetName(doc.getStreetName())
            .recordStatus(RecordStatus.valueOf(doc.getRecordStatus()))
            .createdAt(doc.getCreatedAt())
            .createdBy(doc.getCreatedBy())
            .updatedAt(doc.getUpdatedAt())
            .updatedBy(doc.getUpdatedBy())
            .build();
    }
}