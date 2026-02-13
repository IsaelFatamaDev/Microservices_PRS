package pe.edu.vallegrande.vgmsinfrastructure.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.jackson.Jacksonized;

@Getter
@Builder
@Jacksonized
public class CreateWaterBoxRequest {
    @NotBlank(message = "Organization ID is required")
    private final String organizationId;
    @NotBlank(message = "Box code is required")
    private final String boxCode;
    @NotBlank(message = "Box type is required")
    private final String boxType;
    private final String installationDate;
    private final String zoneId;
    private final String streetId;
    private final String address;
}
