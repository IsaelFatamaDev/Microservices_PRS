package pe.edu.vallegrande.vgmsclaims.application.dto.complaintcategory;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

/**
 * Response DTO for complaint categories.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ComplaintCategoryResponse {

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
}
