package pe.edu.vallegrande.vgmsclaims.application.mappers;

import org.springframework.stereotype.Component;
import pe.edu.vallegrande.vgmsclaims.domain.models.IncidentResolution;
import pe.edu.vallegrande.vgmsclaims.domain.models.valueobjects.MaterialUsed;
import pe.edu.vallegrande.vgmsclaims.domain.models.valueobjects.ResolutionType;
import pe.edu.vallegrande.vgmsclaims.infrastructure.persistence.documents.IncidentResolutionDocument;

import java.util.Collections;
import java.util.stream.Collectors;

/**
 * Mapper for IncidentResolution domain model and documents.
 */
@Component
public class IncidentResolutionMapper {

     public IncidentResolution toDomain(IncidentResolutionDocument document) {
          return IncidentResolution.builder()
                    .id(document.getId())
                    .incidentId(document.getIncidentId())
                    .resolutionDate(document.getResolutionDate())
                    .resolutionType(parseResolutionType(document.getResolutionType()))
                    .actionsTaken(document.getActionsTaken())
                    .materialsUsed(document.getMaterialsUsed() != null ? document.getMaterialsUsed().stream()
                              .map(m -> new MaterialUsed(m.getProductId(), m.getQuantity(), m.getUnit(),
                                        m.getUnitCost()))
                              .collect(Collectors.toList()) : Collections.emptyList())
                    .laborHours(document.getLaborHours())
                    .totalCost(document.getTotalCost())
                    .resolvedByUserId(document.getResolvedByUserId())
                    .qualityCheck(document.getQualityCheck())
                    .followUpRequired(document.getFollowUpRequired())
                    .resolutionNotes(document.getResolutionNotes())
                    .createdAt(document.getCreatedAt())
                    .updatedAt(document.getUpdatedAt())
                    .build();
     }

     public IncidentResolutionDocument toDocument(IncidentResolution domain) {
          return IncidentResolutionDocument.builder()
                    .id(domain.getId())
                    .incidentId(domain.getIncidentId())
                    .resolutionDate(domain.getResolutionDate())
                    .resolutionType(domain.getResolutionType() != null ? domain.getResolutionType().name() : null)
                    .actionsTaken(domain.getActionsTaken())
                    .materialsUsed(domain.getMaterialsUsed() != null ? domain.getMaterialsUsed().stream()
                              .map(m -> IncidentResolutionDocument.MaterialUsedEmbedded.builder()
                                        .productId(m.getProductId())
                                        .quantity(m.getQuantity())
                                        .unit(m.getUnit())
                                        .unitCost(m.getUnitCost())
                                        .build())
                              .collect(Collectors.toList()) : Collections.emptyList())
                    .laborHours(domain.getLaborHours())
                    .totalCost(domain.getTotalCost())
                    .resolvedByUserId(domain.getResolvedByUserId())
                    .qualityCheck(domain.getQualityCheck())
                    .followUpRequired(domain.getFollowUpRequired())
                    .resolutionNotes(domain.getResolutionNotes())
                    .createdAt(domain.getCreatedAt())
                    .updatedAt(domain.getUpdatedAt())
                    .build();
     }

     private ResolutionType parseResolutionType(String type) {
          if (type == null)
               return null;
          try {
               return ResolutionType.valueOf(type.toUpperCase());
          } catch (IllegalArgumentException e) {
               return null;
          }
     }
}
