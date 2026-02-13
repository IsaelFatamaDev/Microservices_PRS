package pe.edu.vallegrande.vgmsclaims.application.dto.complaint;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

/**
 * DTO to update an existing complaint.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateComplaintRequest {

    private String subject;
    private String description;
    private String priority;
    private String status;
    private String assignedToUserId;
    private Instant expectedResolutionDate;
}
