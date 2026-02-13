package pe.edu.vallegrande.vgmsclaims.application.mappers;

import org.springframework.stereotype.Component;
import pe.edu.vallegrande.vgmsclaims.application.dto.incident.CreateIncidentRequest;
import pe.edu.vallegrande.vgmsclaims.application.dto.incident.IncidentResponse;
import pe.edu.vallegrande.vgmsclaims.domain.models.Incident;
import pe.edu.vallegrande.vgmsclaims.domain.models.valueobjects.IncidentSeverity;
import pe.edu.vallegrande.vgmsclaims.domain.models.valueobjects.IncidentStatus;
import pe.edu.vallegrande.vgmsclaims.domain.models.valueobjects.RecordStatus;
import pe.edu.vallegrande.vgmsclaims.infrastructure.persistence.documents.IncidentDocument;

/**
 * Mapper for Incident conversions
 */
@Component
public class IncidentMapper {
    
    /**
     * Converts a creation request to domain model
     */
    public Incident toDomain(CreateIncidentRequest request) {
        return Incident.builder()
                .incidentCode(request.getIncidentCode())
                .title(request.getTitle())
                .description(request.getDescription())
                .incidentTypeId(request.getIncidentTypeId())
                .incidentCategory(request.getIncidentCategory())
                .zoneId(request.getZoneId())
                .incidentDate(request.getIncidentDate())
                .severity(parseIncidentSeverity(request.getSeverity()))
                .status(parseIncidentStatus(request.getStatus()))
                .affectedBoxesCount(request.getAffectedBoxesCount())
                .organizationId(request.getOrganizationId())
                .reportedByUserId(request.getReportedByUserId())
                .assignedToUserId(request.getAssignedToUserId())
                .resolvedByUserId(request.getResolvedByUserId())
                .resolved(request.getResolved())
                .resolutionNotes(request.getResolutionNotes())
                .recordStatus(parseRecordStatus(request.getRecordStatus()))
                .build();
    }
    
    /**
     * Converts a document to domain model
     */
    public Incident toDomain(IncidentDocument document) {
        return Incident.builder()
                .id(document.getId())
                .organizationId(document.getOrganizationId())
                .incidentCode(document.getIncidentCode())
                .incidentTypeId(document.getIncidentTypeId())
                .incidentCategory(document.getIncidentCategory())
                .zoneId(document.getZoneId())
                .incidentDate(document.getIncidentDate())
                .title(document.getTitle())
                .description(document.getDescription())
                .severity(parseIncidentSeverity(document.getSeverity()))
                .status(parseIncidentStatus(document.getStatus()))
                .affectedBoxesCount(document.getAffectedBoxesCount())
                .reportedByUserId(document.getReportedByUserId())
                .assignedToUserId(document.getAssignedToUserId())
                .resolvedByUserId(document.getResolvedByUserId())
                .resolved(document.getResolved())
                .resolutionNotes(document.getResolutionNotes())
                .recordStatus(parseRecordStatus(document.getRecordStatus()))
                .createdAt(document.getCreatedAt())
                .updatedAt(document.getUpdatedAt())
                .build();
    }
    
    /**
     * Converts a domain model to document
     */
    public IncidentDocument toDocument(Incident domain) {
        IncidentDocument document = new IncidentDocument();
        document.setId(domain.getId());
        document.setOrganizationId(domain.getOrganizationId());
        document.setIncidentCode(domain.getIncidentCode());
        document.setIncidentTypeId(domain.getIncidentTypeId());
        document.setIncidentCategory(domain.getIncidentCategory());
        document.setZoneId(domain.getZoneId());
        document.setIncidentDate(domain.getIncidentDate());
        document.setTitle(domain.getTitle());
        document.setDescription(domain.getDescription());
        document.setSeverity(domain.getSeverity() != null ? domain.getSeverity().name() : null);
        document.setStatus(domain.getStatus() != null ? domain.getStatus().name() : null);
        document.setAffectedBoxesCount(domain.getAffectedBoxesCount());
        document.setReportedByUserId(domain.getReportedByUserId());
        document.setAssignedToUserId(domain.getAssignedToUserId());
        document.setResolvedByUserId(domain.getResolvedByUserId());
        document.setResolved(domain.getResolved());
        document.setResolutionNotes(domain.getResolutionNotes());
        document.setRecordStatus(domain.getRecordStatus() != null ? domain.getRecordStatus().name() : null);
        document.setCreatedAt(domain.getCreatedAt());
        document.setUpdatedAt(domain.getUpdatedAt());
        return document;
    }
    
    /**
     * Converts a domain model to response DTO
     */
    public IncidentResponse toResponse(Incident domain) {
        return IncidentResponse.builder()
                .id(domain.getId())
                .organizationId(domain.getOrganizationId())
                .incidentCode(domain.getIncidentCode())
                .incidentTypeId(domain.getIncidentTypeId())
                .incidentCategory(domain.getIncidentCategory())
                .zoneId(domain.getZoneId())
                .incidentDate(domain.getIncidentDate())
                .title(domain.getTitle())
                .description(domain.getDescription())
                .severity(domain.getSeverity() != null ? domain.getSeverity().name() : null)
                .status(domain.getStatus() != null ? domain.getStatus().name() : null)
                .affectedBoxesCount(domain.getAffectedBoxesCount())
                .reportedByUserId(domain.getReportedByUserId())
                .assignedToUserId(domain.getAssignedToUserId())
                .resolvedByUserId(domain.getResolvedByUserId())
                .resolved(domain.getResolved())
                .resolutionNotes(domain.getResolutionNotes())
                .recordStatus(domain.getRecordStatus() != null ? domain.getRecordStatus().name() : null)
                .createdAt(domain.getCreatedAt())
                .updatedAt(domain.getUpdatedAt())
                .build();
    }
    
    private IncidentSeverity parseIncidentSeverity(String severity) {
        if (severity == null) return null;
        try {
            return IncidentSeverity.valueOf(severity.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
    
    private IncidentStatus parseIncidentStatus(String status) {
        if (status == null) return null;
        try {
            return IncidentStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
    
    private RecordStatus parseRecordStatus(String status) {
        if (status == null) return null;
        try {
            return RecordStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
