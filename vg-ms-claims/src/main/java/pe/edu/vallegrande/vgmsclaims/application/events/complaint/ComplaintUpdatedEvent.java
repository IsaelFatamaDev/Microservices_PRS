package pe.edu.vallegrande.vgmsclaims.application.events.complaint;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

/**
 * Event emitted when a complaint is updated
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ComplaintUpdatedEvent {
    
    private String complaintId;
    private String complaintCode;
    private String status;
    private Instant updatedAt;
}
