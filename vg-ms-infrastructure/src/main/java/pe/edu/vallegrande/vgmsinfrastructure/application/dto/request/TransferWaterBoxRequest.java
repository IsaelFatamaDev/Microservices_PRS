package pe.edu.vallegrande.vgmsinfrastructure.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.jackson.Jacksonized;

@Getter
@Builder
@Jacksonized
public class TransferWaterBoxRequest {
    @NotBlank(message = "Water box ID is required")
    private final String waterBoxId;
    @NotBlank(message = "From user ID is required")
    private final String fromUserId;
    @NotBlank(message = "To user ID is required")
    private final String toUserId;
    private final Double transferFee;
    private final String notes;
}
