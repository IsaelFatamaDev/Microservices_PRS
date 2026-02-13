package pe.edu.vallegrande.vgmsinfrastructure.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.jackson.Jacksonized;

@Getter
@Builder
@Jacksonized
public class AssignWaterBoxRequest {
    @NotBlank(message = "Water box ID is required")
    private final String waterBoxId;
    @NotBlank(message = "User ID is required")
    private final String userId;
}
