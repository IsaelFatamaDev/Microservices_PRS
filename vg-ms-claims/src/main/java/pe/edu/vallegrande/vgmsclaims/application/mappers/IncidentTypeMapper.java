package pe.edu.vallegrande.vgmsclaims.application.mappers;

import org.springframework.stereotype.Component;
import pe.edu.vallegrande.vgmsclaims.application.dto.incidenttype.CreateIncidentTypeRequest;
import pe.edu.vallegrande.vgmsclaims.application.dto.incidenttype.IncidentTypeResponse;
import pe.edu.vallegrande.vgmsclaims.domain.models.IncidentType;
import pe.edu.vallegrande.vgmsclaims.domain.models.valueobjects.RecordStatus;
import pe.edu.vallegrande.vgmsclaims.infrastructure.persistence.documents.IncidentTypeDocument;

/**
 * Mapper for IncidentType domain model, DTOs, and documents.
 */
@Component
public class IncidentTypeMapper {

     public IncidentType toDomain(CreateIncidentTypeRequest request) {
          return IncidentType.builder()
                    .typeCode(request.getTypeCode())
                    .typeName(request.getTypeName())
                    .description(request.getDescription())
                    .priorityLevel(request.getPriorityLevel())
                    .estimatedResolutionTime(request.getEstimatedResolutionTime())
                    .requiresExternalService(request.getRequiresExternalService())
                    .organizationId(request.getOrganizationId())
                    .build();
     }

     public IncidentType toDomain(IncidentTypeDocument document) {
          return IncidentType.builder()
                    .id(document.getId())
                    .organizationId(document.getOrganizationId())
                    .typeCode(document.getTypeCode())
                    .typeName(document.getTypeName())
                    .description(document.getDescription())
                    .priorityLevel(document.getPriorityLevel())
                    .estimatedResolutionTime(document.getEstimatedResolutionTime())
                    .requiresExternalService(document.getRequiresExternalService())
                    .recordStatus(parseRecordStatus(document.getRecordStatus()))
                    .createdAt(document.getCreatedAt())
                    .updatedAt(document.getUpdatedAt())
                    .build();
     }

     public IncidentTypeDocument toDocument(IncidentType domain) {
          return IncidentTypeDocument.builder()
                    .id(domain.getId())
                    .organizationId(domain.getOrganizationId())
                    .typeCode(domain.getTypeCode())
                    .typeName(domain.getTypeName())
                    .description(domain.getDescription())
                    .priorityLevel(domain.getPriorityLevel())
                    .estimatedResolutionTime(domain.getEstimatedResolutionTime())
                    .requiresExternalService(domain.getRequiresExternalService())
                    .recordStatus(domain.getRecordStatus() != null ? domain.getRecordStatus().name() : null)
                    .createdAt(domain.getCreatedAt())
                    .updatedAt(domain.getUpdatedAt())
                    .build();
     }

     public IncidentTypeResponse toResponse(IncidentType domain) {
          return IncidentTypeResponse.builder()
                    .id(domain.getId())
                    .organizationId(domain.getOrganizationId())
                    .typeCode(domain.getTypeCode())
                    .typeName(domain.getTypeName())
                    .description(domain.getDescription())
                    .priorityLevel(domain.getPriorityLevel())
                    .estimatedResolutionTime(domain.getEstimatedResolutionTime())
                    .requiresExternalService(domain.getRequiresExternalService())
                    .recordStatus(domain.getRecordStatus() != null ? domain.getRecordStatus().name() : null)
                    .createdAt(domain.getCreatedAt())
                    .updatedAt(domain.getUpdatedAt())
                    .build();
     }

     private RecordStatus parseRecordStatus(String status) {
          if (status == null)
               return null;
          try {
               return RecordStatus.valueOf(status.toUpperCase());
          } catch (IllegalArgumentException e) {
               return null;
          }
     }
}
