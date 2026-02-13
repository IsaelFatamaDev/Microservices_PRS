package pe.edu.vallegrande.vgmsclaims.application.mappers;

import org.springframework.stereotype.Component;
import pe.edu.vallegrande.vgmsclaims.application.dto.complaint.CreateComplaintRequest;
import pe.edu.vallegrande.vgmsclaims.application.dto.complaint.ComplaintResponse;
import pe.edu.vallegrande.vgmsclaims.domain.models.Complaint;
import pe.edu.vallegrande.vgmsclaims.domain.models.valueobjects.ComplaintPriority;
import pe.edu.vallegrande.vgmsclaims.infrastructure.persistence.documents.ComplaintDocument;

/**
 * Mapper for Complaint conversions
 */
@Component
public class ComplaintMapper {
    
    /**
     * Converts a creation request to domain model
     */
    public Complaint toDomain(CreateComplaintRequest request) {
        return Complaint.builder()
                .subject(request.getSubject())
                .description(request.getDescription())
                .categoryId(request.getCategoryId())
                .waterBoxId(request.getWaterBoxId())
                .organizationId(request.getOrganizationId())
                .priority(parseComplaintPriority(request.getPriority()))
                .build();
    }
    
    /**
     * Converts a document to domain model
     */
    public Complaint toDomain(ComplaintDocument document) {
        return Complaint.builder()
                .id(document.getId())
                .organizationId(document.getOrganizationId())
                .complaintCode(document.getComplaintCode())
                .userId(document.getUserId())
                .categoryId(document.getCategoryId())
                .waterBoxId(document.getWaterBoxId())
                .complaintDate(document.getComplaintDate())
                .subject(document.getSubject())
                .description(document.getDescription())
                .priority(parseComplaintPriority(document.getPriority()))
                .status(parseComplaintStatus(document.getStatus()))
                .assignedToUserId(document.getAssignedToUserId())
                .expectedResolutionDate(document.getExpectedResolutionDate())
                .actualResolutionDate(document.getActualResolutionDate())
                .satisfactionRating(document.getSatisfactionRating())
                .recordStatus(parseRecordStatus(document.getRecordStatus()))
                .createdAt(document.getCreatedAt())
                .updatedAt(document.getUpdatedAt())
                .build();
    }
    
    /**
     * Converts a domain model to document
     */
    public ComplaintDocument toDocument(Complaint domain) {
        ComplaintDocument document = new ComplaintDocument();
        document.setId(domain.getId());
        document.setOrganizationId(domain.getOrganizationId());
        document.setComplaintCode(domain.getComplaintCode());
        document.setUserId(domain.getUserId());
        document.setCategoryId(domain.getCategoryId());
        document.setWaterBoxId(domain.getWaterBoxId());
        document.setComplaintDate(domain.getComplaintDate());
        document.setSubject(domain.getSubject());
        document.setDescription(domain.getDescription());
        document.setPriority(domain.getPriority() != null ? domain.getPriority().name() : null);
        document.setStatus(domain.getStatus() != null ? domain.getStatus().name() : null);
        document.setAssignedToUserId(domain.getAssignedToUserId());
        document.setExpectedResolutionDate(domain.getExpectedResolutionDate());
        document.setActualResolutionDate(domain.getActualResolutionDate());
        document.setSatisfactionRating(domain.getSatisfactionRating());
        document.setRecordStatus(domain.getRecordStatus() != null ? domain.getRecordStatus().name() : null);
        document.setCreatedAt(domain.getCreatedAt());
        document.setUpdatedAt(domain.getUpdatedAt());
        return document;
    }
    
    /**
     * Converts a domain model to response DTO
     */
    public ComplaintResponse toResponse(Complaint domain) {
        return ComplaintResponse.builder()
                .id(domain.getId())
                .organizationId(domain.getOrganizationId())
                .complaintCode(domain.getComplaintCode())
                .userId(domain.getUserId())
                .categoryId(domain.getCategoryId())
                .waterBoxId(domain.getWaterBoxId())
                .complaintDate(domain.getComplaintDate())
                .subject(domain.getSubject())
                .description(domain.getDescription())
                .priority(domain.getPriority() != null ? domain.getPriority().name() : null)
                .status(domain.getStatus() != null ? domain.getStatus().name() : null)
                .assignedToUserId(domain.getAssignedToUserId())
                .expectedResolutionDate(domain.getExpectedResolutionDate())
                .actualResolutionDate(domain.getActualResolutionDate())
                .satisfactionRating(domain.getSatisfactionRating())
                .recordStatus(domain.getRecordStatus() != null ? domain.getRecordStatus().name() : null)
                .createdAt(domain.getCreatedAt())
                .updatedAt(domain.getUpdatedAt())
                .build();
    }
    
    private ComplaintPriority parseComplaintPriority(String priority) {
        if (priority == null) return null;
        try {
            return ComplaintPriority.valueOf(priority.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
    
    private pe.edu.vallegrande.vgmsclaims.domain.models.valueobjects.ComplaintStatus parseComplaintStatus(String status) {
        if (status == null) return null;
        try {
            return pe.edu.vallegrande.vgmsclaims.domain.models.valueobjects.ComplaintStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
    
    private pe.edu.vallegrande.vgmsclaims.domain.models.valueobjects.RecordStatus parseRecordStatus(String status) {
        if (status == null) return null;
        try {
            return pe.edu.vallegrande.vgmsclaims.domain.models.valueobjects.RecordStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
