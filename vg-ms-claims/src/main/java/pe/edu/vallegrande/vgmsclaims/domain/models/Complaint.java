package pe.edu.vallegrande.vgmsclaims.domain.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pe.edu.vallegrande.vgmsclaims.domain.models.valueobjects.ComplaintPriority;
import pe.edu.vallegrande.vgmsclaims.domain.models.valueobjects.ComplaintStatus;
import pe.edu.vallegrande.vgmsclaims.domain.models.valueobjects.RecordStatus;

import java.time.Instant;

/**
 * Domain entity representing a Complaint.
 * Does not contain infrastructure annotations (MongoDB, JPA, etc.)
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Complaint {
    
    private String id;
    private String organizationId;
    private String complaintCode;
    private String userId;
    private String categoryId;
    private String waterBoxId;
    private Instant complaintDate;
    private String subject;
    private String description;
    private ComplaintPriority priority;
    private ComplaintStatus status;
    private String assignedToUserId;
    private Instant expectedResolutionDate;
    private Instant actualResolutionDate;
    private Integer satisfactionRating;
    
    @Builder.Default
    private RecordStatus recordStatus = RecordStatus.ACTIVE;
    
    private Instant createdAt;
    private Instant updatedAt;

    /**
     * Checks if complaint can be assigned
     */
    public boolean canBeAssigned() {
        return status == ComplaintStatus.RECEIVED && recordStatus == RecordStatus.ACTIVE;
    }

    /**
     * Checks if complaint can be resolved
     */
    public boolean canBeResolved() {
        return (status == ComplaintStatus.IN_PROGRESS || status == ComplaintStatus.RECEIVED) 
                && recordStatus == RecordStatus.ACTIVE;
    }

    /**
     * Checks if complaint can be closed
     */
    public boolean canBeClosed() {
        return status == ComplaintStatus.RESOLVED && recordStatus == RecordStatus.ACTIVE;
    }

    /**
     * Checks if complaint is active
     */
    public boolean isActive() {
        return recordStatus == RecordStatus.ACTIVE;
    }

    /**
     * Assigns a user to the complaint
     */
    public void assignTo(String userId) {
        if (!canBeAssigned()) {
            throw new IllegalStateException("The complaint cannot be assigned in its current state");
        }
        this.assignedToUserId = userId;
        this.status = ComplaintStatus.IN_PROGRESS;
        this.updatedAt = Instant.now();
    }

    /**
     * Resolves the complaint
     */
    public void resolve() {
        if (!canBeResolved()) {
            throw new IllegalStateException("The complaint cannot be resolved in its current state");
        }
        this.status = ComplaintStatus.RESOLVED;
        this.actualResolutionDate = Instant.now();
        this.updatedAt = Instant.now();
    }

    /**
     * Closes the complaint
     */
    public void close(Integer satisfactionRating) {
        if (!canBeClosed()) {
            throw new IllegalStateException("The complaint cannot be closed in its current state");
        }
        this.status = ComplaintStatus.CLOSED;
        this.satisfactionRating = satisfactionRating;
        this.updatedAt = Instant.now();
    }
}
