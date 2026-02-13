package pe.edu.vallegrande.vgmsclaims.infrastructure.persistence.documents;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

/**
 * MongoDB document for complaint categories
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "complaint_categories")
public class ComplaintCategoryDocument {
    
    @Id
    private String id;
    private String organizationId;
    private String categoryCode;
    private String categoryName;
    private String description;
    private String priorityLevel;
    private Integer maxResponseTime;
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
