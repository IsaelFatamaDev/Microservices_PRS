package pe.edu.vallegrande.vgmsclaims.application.dto.complaintcategory;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO to create a new complaint category.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateComplaintCategoryRequest {

    @NotBlank(message = "Category code is required")
    private String categoryCode;

    @NotBlank(message = "Category name is required")
    private String categoryName;

    private String description;

    private String priorityLevel;

    private Integer maxResponseTime;

    private String organizationId;
}
