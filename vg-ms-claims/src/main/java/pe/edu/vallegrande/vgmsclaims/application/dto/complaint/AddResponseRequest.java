package pe.edu.vallegrande.vgmsclaims.application.dto.complaint;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO to add a response to a complaint.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddResponseRequest {

    @NotNull(message = "Response type is required")
    private String responseType;

    @NotBlank(message = "Message is required")
    private String message;

    private String internalNotes;
}
