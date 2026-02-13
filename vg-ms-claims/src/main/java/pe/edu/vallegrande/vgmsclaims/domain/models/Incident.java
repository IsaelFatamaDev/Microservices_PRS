package pe.edu.vallegrande.vgmsclaims.domain.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pe.edu.vallegrande.vgmsclaims.domain.models.valueobjects.IncidentSeverity;
import pe.edu.vallegrande.vgmsclaims.domain.models.valueobjects.IncidentStatus;
import pe.edu.vallegrande.vgmsclaims.domain.models.valueobjects.RecordStatus;

import java.time.Instant;

/**
 * Domain entity representing an infrastructure Incident.
 * Does not contain infrastructure annotations (MongoDB, JPA, etc.)
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Incident {
    
    private String id;
    private String organizationId;
    private String incidentCode;
    private String incidentTypeId;
    private String incidentCategory;
    private String zoneId;
    private Instant incidentDate;
    private String title;
    private String description;
    private IncidentSeverity severity;
    private IncidentStatus status;
    private Integer affectedBoxesCount;
    private String reportedByUserId;
    private String assignedToUserId;
    private String resolvedByUserId;
    
    @Builder.Default
    private Boolean resolved = false;
    
    private String resolutionNotes;
    
    @Builder.Default
    private RecordStatus recordStatus = RecordStatus.ACTIVE;
    
    private Instant createdAt;
    private Instant updatedAt;

    /**
     * Checks if incident is critical
     */
    public boolean isCritical() {
        return severity == IncidentSeverity.CRITICAL;
    }

    /**
     * Checks if incident is active
     */
    public boolean isActive() {
        return recordStatus == RecordStatus.ACTIVE;
    }

    /**
     * Checks if incident can be assigned
     */
    public boolean canBeAssigned() {
        return status == IncidentStatus.REPORTED && isActive();
    }

    /**
     * Checks if incident can be resolved
     */
    public boolean canBeResolved() {
        return (status == IncidentStatus.ASSIGNED || status == IncidentStatus.IN_PROGRESS) && isActive();
    }

    /**
     * Checks if incident can be closed
     */
    public boolean canBeClosed() {
        return status == IncidentStatus.RESOLVED && isActive();
    }

    /**
     * Assigns a technician to the incident
     */
    public void assignTo(String userId) {
        if (!canBeAssigned()) {
            throw new IllegalStateException("The incident cannot be assigned in its current state");
        }
        this.assignedToUserId = userId;
        this.status = IncidentStatus.ASSIGNED;
        this.updatedAt = Instant.now();
    }

    /**
     * Marks the incident as in progress
     */
    public void startProgress() {
        if (status != IncidentStatus.ASSIGNED) {
            throw new IllegalStateException("The incident must be assigned to start progress");
        }
        this.status = IncidentStatus.IN_PROGRESS;
        this.updatedAt = Instant.now();
    }

    /**
     * Resolves the incident
     */
    public void resolve(String userId, String notes) {
        if (!canBeResolved()) {
            throw new IllegalStateException("The incident cannot be resolved in its current state");
        }
        this.resolvedByUserId = userId;
        this.resolutionNotes = notes;
        this.resolved = true;
        this.status = IncidentStatus.RESOLVED;
        this.updatedAt = Instant.now();
    }

    /**
     * Closes the incident
     */
    public void close() {
        if (!canBeClosed()) {
            throw new IllegalStateException("The incident cannot be closed in its current state");
        }
        this.status = IncidentStatus.CLOSED;
        this.updatedAt = Instant.now();
    }
}
