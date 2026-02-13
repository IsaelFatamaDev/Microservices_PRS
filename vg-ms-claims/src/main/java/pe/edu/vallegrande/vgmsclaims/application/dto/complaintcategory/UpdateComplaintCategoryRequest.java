package pe.edu.vallegrande.vgmsclaims.application.dto.complaintcategory;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO to update an existing complaint category.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateComplaintCategoryRequest {

    private String categoryName;
    private String description;
    private String priorityLevel;
    private Integer maxResponseTime;
}
