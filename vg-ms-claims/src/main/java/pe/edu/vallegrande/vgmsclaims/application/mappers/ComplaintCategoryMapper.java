package pe.edu.vallegrande.vgmsclaims.application.mappers;

import org.springframework.stereotype.Component;
import pe.edu.vallegrande.vgmsclaims.application.dto.complaintcategory.ComplaintCategoryResponse;
import pe.edu.vallegrande.vgmsclaims.application.dto.complaintcategory.CreateComplaintCategoryRequest;
import pe.edu.vallegrande.vgmsclaims.domain.models.ComplaintCategory;
import pe.edu.vallegrande.vgmsclaims.domain.models.valueobjects.RecordStatus;
import pe.edu.vallegrande.vgmsclaims.infrastructure.persistence.documents.ComplaintCategoryDocument;

/**
 * Mapper for ComplaintCategory domain model, DTOs, and documents.
 */
@Component
public class ComplaintCategoryMapper {

     public ComplaintCategory toDomain(CreateComplaintCategoryRequest request) {
          return ComplaintCategory.builder()
                    .categoryCode(request.getCategoryCode())
                    .categoryName(request.getCategoryName())
                    .description(request.getDescription())
                    .priorityLevel(request.getPriorityLevel())
                    .maxResponseTime(request.getMaxResponseTime())
                    .organizationId(request.getOrganizationId())
                    .build();
     }

     public ComplaintCategory toDomain(ComplaintCategoryDocument document) {
          return ComplaintCategory.builder()
                    .id(document.getId())
                    .organizationId(document.getOrganizationId())
                    .categoryCode(document.getCategoryCode())
                    .categoryName(document.getCategoryName())
                    .description(document.getDescription())
                    .priorityLevel(document.getPriorityLevel())
                    .maxResponseTime(document.getMaxResponseTime())
                    .recordStatus(parseRecordStatus(document.getRecordStatus()))
                    .createdAt(document.getCreatedAt())
                    .updatedAt(document.getUpdatedAt())
                    .build();
     }

     public ComplaintCategoryDocument toDocument(ComplaintCategory domain) {
          return ComplaintCategoryDocument.builder()
                    .id(domain.getId())
                    .organizationId(domain.getOrganizationId())
                    .categoryCode(domain.getCategoryCode())
                    .categoryName(domain.getCategoryName())
                    .description(domain.getDescription())
                    .priorityLevel(domain.getPriorityLevel())
                    .maxResponseTime(domain.getMaxResponseTime())
                    .recordStatus(domain.getRecordStatus() != null ? domain.getRecordStatus().name() : null)
                    .createdAt(domain.getCreatedAt())
                    .updatedAt(domain.getUpdatedAt())
                    .build();
     }

     public ComplaintCategoryResponse toResponse(ComplaintCategory domain) {
          return ComplaintCategoryResponse.builder()
                    .id(domain.getId())
                    .organizationId(domain.getOrganizationId())
                    .categoryCode(domain.getCategoryCode())
                    .categoryName(domain.getCategoryName())
                    .description(domain.getDescription())
                    .priorityLevel(domain.getPriorityLevel())
                    .maxResponseTime(domain.getMaxResponseTime())
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
