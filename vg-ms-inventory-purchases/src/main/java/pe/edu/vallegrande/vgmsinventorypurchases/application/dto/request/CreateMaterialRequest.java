package pe.edu.vallegrande.vgmsinventorypurchases.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.jackson.Jacksonized;

@Getter
@Builder
@Jacksonized
public class CreateMaterialRequest {
    @NotBlank(message = "Organization ID is required")
    private final String organizationId;
    @NotBlank(message = "Material code is required")
    private final String materialCode;
    @NotBlank(message = "Material name is required")
    private final String materialName;
    private final String categoryId;
    @NotBlank(message = "Unit is required")
    @Builder.Default
    private final String unit = "UNIT";
    private final Double currentStock;
    private final Double minStock;
    private final Double unitPrice;
}
