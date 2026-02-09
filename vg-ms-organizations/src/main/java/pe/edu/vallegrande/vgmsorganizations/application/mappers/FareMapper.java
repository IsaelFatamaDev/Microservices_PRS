package pe.edu.vallegrande.vgmsorganizations.application.mappers;

import org.springframework.stereotype.Component;
import pe.edu.vallegrande.vgmsorganizations.application.dto.request.CreateFareRequest;
import pe.edu.vallegrande.vgmsorganizations.application.dto.request.UpdateFareRequest;
import pe.edu.vallegrande.vgmsorganizations.application.dto.response.FareResponse;
import pe.edu.vallegrande.vgmsorganizations.domain.models.Fare;
import pe.edu.vallegrande.vgmsorganizations.domain.models.valueobjects.RecordStatus;
import pe.edu.vallegrande.vgmsorganizations.infrastructure.persistence.documents.FareDocument;

@Component
public class FareMapper {

    public Fare toModel(CreateFareRequest request) {
        return Fare.builder()
            .organizationId(request.getOrganizationId())
            .fareType(request.getFareType())
            .amount(request.getAmount())
            .description(request.getDescription())
            .validFrom(request.getValidFrom())
            .validTo(request.getValidTo())
            .build();
    }

    public Fare toModel(UpdateFareRequest request) {
        return Fare.builder()
            .fareType(request.getFareType())
            .amount(request.getAmount())
            .description(request.getDescription())
            .validFrom(request.getValidFrom())
            .validTo(request.getValidTo())
            .build();
    }

    public FareResponse toResponse(Fare fare) {
        return FareResponse.builder()
            .id(fare.getId())
            .organizationId(fare.getOrganizationId())
            .fareType(fare.getFareType())
            .fareTypeDisplayName(fare.getFareType())
            .amount(fare.getAmount())
            .description(fare.getDescription())
            .validFrom(fare.getValidFrom())
            .validTo(fare.getValidTo())
            .currentlyValid(fare.isCurrentlyValid())
            .recordStatus(fare.getRecordStatus().name())
            .createdAt(fare.getCreatedAt())
            .createdBy(fare.getCreatedBy())
            .updatedAt(fare.getUpdatedAt())
            .updatedBy(fare.getUpdatedBy())
            .build();
    }

    public FareDocument toDocument(Fare fare) {
        return FareDocument.builder()
            .id(fare.getId())
            .organizationId(fare.getOrganizationId())
            .fareType(fare.getFareType())
            .amount(fare.getAmount())
            .description(fare.getDescription())
            .validFrom(fare.getValidFrom())
            .validTo(fare.getValidTo())
            .recordStatus(fare.getRecordStatus().name())
            .createdAt(fare.getCreatedAt())
            .createdBy(fare.getCreatedBy())
            .updatedAt(fare.getUpdatedAt())
            .updatedBy(fare.getUpdatedBy())
            .build();
    }

    public Fare toModel(FareDocument doc) {
        return Fare.builder()
            .id(doc.getId())
            .organizationId(doc.getOrganizationId())
            .fareType(doc.getFareType())
            .amount(doc.getAmount())
            .description(doc.getDescription())
            .validFrom(doc.getValidFrom())
            .validTo(doc.getValidTo())
            .recordStatus(RecordStatus.valueOf(doc.getRecordStatus()))
            .createdAt(doc.getCreatedAt())
            .createdBy(doc.getCreatedBy())
            .updatedAt(doc.getUpdatedAt())
            .updatedBy(doc.getUpdatedBy())
            .build();
    }
}