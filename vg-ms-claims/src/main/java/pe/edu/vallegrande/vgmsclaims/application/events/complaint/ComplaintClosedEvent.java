package pe.edu.vallegrande.vgmsclaims.application.events.complaint;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

/**
 * Event emitted when a complaint is closed
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ComplaintClosedEvent {
    
    private String complaintId;
    private String complaintCode;
    private Integer satisfactionRating;
    private Instant closedAt;
}
