package pe.edu.vallegrande.vgmsclaims.application.dto.complaint;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

/**
 * Response DTO for complaints.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ComplaintResponse {

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
}
