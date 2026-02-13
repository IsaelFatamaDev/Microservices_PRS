package pe.edu.vallegrande.vgmsinfrastructure.application.dto.request;

import lombok.Builder;
import lombok.Getter;
import lombok.extern.jackson.Jacksonized;

@Getter
@Builder
@Jacksonized
public class UpdateWaterBoxRequest {
    private final String boxCode;
    private final String boxType;
    private final String installationDate;
    private final String zoneId;
    private final String streetId;
    private final String address;
}
