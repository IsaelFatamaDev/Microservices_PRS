package pe.edu.vallegrande.vgmsorganizations.application.mappers;

import org.springframework.stereotype.Component;
import pe.edu.vallegrande.vgmsorganizations.application.dto.request.CreateZoneRequest;
import pe.edu.vallegrande.vgmsorganizations.application.dto.request.UpdateZoneRequest;
import pe.edu.vallegrande.vgmsorganizations.application.dto.response.ZoneResponse;
import pe.edu.vallegrande.vgmsorganizations.domain.models.Zone;
import pe.edu.vallegrande.vgmsorganizations.domain.models.valueobjects.RecordStatus;
import pe.edu.vallegrande.vgmsorganizations.infrastructure.persistence.documents.ZoneDocument;

@Component
public class ZoneMapper {

    public Zone toModel(CreateZoneRequest request) {
        return Zone.builder()
            .organizationId(request.getOrganizationId())
            .zoneName(request.getZoneName())
            .description(request.getDescription())
            .build();
    }

    public Zone toModel(UpdateZoneRequest request) {
        return Zone.builder()
            .zoneName(request.getZoneName())
            .description(request.getDescription())
            .build();
    }

    public ZoneResponse toResponse(Zone zone) {
        return ZoneResponse.builder()
            .id(zone.getId())
            .organizationId(zone.getOrganizationId())
            .zoneName(zone.getZoneName())
            .description(zone.getDescription())
            .recordStatus(zone.getRecordStatus().name())
            .createdAt(zone.getCreatedAt())
            .createdBy(zone.getCreatedBy())
            .updatedAt(zone.getUpdatedAt())
            .updatedBy(zone.getUpdatedBy())
            .build();
    }

    public ZoneDocument toDocument(Zone zone) {
        return ZoneDocument.builder()
            .id(zone.getId())
            .organizationId(zone.getOrganizationId())
            .zoneName(zone.getZoneName())
            .description(zone.getDescription())
            .recordStatus(zone.getRecordStatus().name())
            .createdAt(zone.getCreatedAt())
            .createdBy(zone.getCreatedBy())
            .updatedAt(zone.getUpdatedAt())
            .updatedBy(zone.getUpdatedBy())
            .build();
    }

    public Zone toModel(ZoneDocument doc) {
        return Zone.builder()
            .id(doc.getId())
            .organizationId(doc.getOrganizationId())
            .zoneName(doc.getZoneName())
            .description(doc.getDescription())
            .recordStatus(RecordStatus.valueOf(doc.getRecordStatus()))
            .createdAt(doc.getCreatedAt())
            .createdBy(doc.getCreatedBy())
            .updatedAt(doc.getUpdatedAt())
            .updatedBy(doc.getUpdatedBy())
            .build();
    }
}