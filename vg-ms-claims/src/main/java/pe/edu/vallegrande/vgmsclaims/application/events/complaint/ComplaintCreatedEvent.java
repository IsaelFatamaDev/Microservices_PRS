package pe.edu.vallegrande.vgmsclaims.application.events.complaint;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

/**
 * Event emitted when a complaint is created
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ComplaintCreatedEvent {
    
    private String complaintId;
    private String complaintCode;
    private String userId;
    private String subject;
    private String priority;
    private Instant createdAt;
}
