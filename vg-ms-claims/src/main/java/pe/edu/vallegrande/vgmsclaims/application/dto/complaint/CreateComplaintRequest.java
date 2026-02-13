package pe.edu.vallegrande.vgmsclaims.application.dto.complaint;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO to create a new complaint.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateComplaintRequest {

    @NotBlank(message = "Subject is required")
    private String subject;

    @NotBlank(message = "Description is required")
    private String description;

    @NotNull(message = "Category is required")
    private String categoryId;

    private String waterBoxId;
    private String priority;
    private String organizationId;
}
