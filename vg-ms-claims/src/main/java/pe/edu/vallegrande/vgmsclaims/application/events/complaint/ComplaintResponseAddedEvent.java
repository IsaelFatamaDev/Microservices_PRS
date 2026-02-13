package pe.edu.vallegrande.vgmsclaims.application.events.complaint;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

/**
 * Event emitted when a response is added to a complaint
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ComplaintResponseAddedEvent {
    
    private String responseId;
    private String complaintId;
    private String responseType;
    private String respondedByUserId;
    private Instant createdAt;
}
