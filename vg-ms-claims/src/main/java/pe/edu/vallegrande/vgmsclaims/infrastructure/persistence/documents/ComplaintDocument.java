package pe.edu.vallegrande.vgmsclaims.infrastructure.persistence.documents;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

/**
 * MongoDB document for complaints
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "complaints")
public class ComplaintDocument {
    
    @Id
    private String id;
    private String organizationId;
    private String complaintCode;
    private String userId;
    private String categoryId;
    private String waterBoxId;
    private Instant complaintDate;
    private String subject;
    private String description;
    private String priority;
    private String status;
    private String assignedToUserId;
    private Instant expectedResolutionDate;
    private Instant actualResolutionDate;
    private Integer satisfactionRating;
    private String recordStatus;
    private Instant createdAt;
    private Instant updatedAt;
    
    public void prePersist() {
        if (this.createdAt == null) {
            this.createdAt = Instant.now();
        }
        this.updatedAt = Instant.now();
    }
}
