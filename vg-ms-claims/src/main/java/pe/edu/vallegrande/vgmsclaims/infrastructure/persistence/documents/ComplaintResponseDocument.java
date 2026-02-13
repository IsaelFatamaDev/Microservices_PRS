package pe.edu.vallegrande.vgmsclaims.infrastructure.persistence.documents;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

/**
 * MongoDB document for complaint responses
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "complaint_responses")
public class ComplaintResponseDocument {
    
    @Id
    private String id;
    private String complaintId;
    private Instant responseDate;
    private String responseType;
    private String message;
    private String respondedByUserId;
    private String internalNotes;
    private Instant createdAt;
    private Instant updatedAt;
    
    public void prePersist() {
        if (this.createdAt == null) {
            this.createdAt = Instant.now();
        }
        this.updatedAt = Instant.now();
    }
}
