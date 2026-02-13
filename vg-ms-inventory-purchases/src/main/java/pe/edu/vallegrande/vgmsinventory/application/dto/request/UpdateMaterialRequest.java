package pe.edu.vallegrande.vgmsinventorypurchases.application.dto.request;

import lombok.Builder;
import lombok.Getter;
import lombok.extern.jackson.Jacksonized;

@Getter
@Builder
@Jacksonized
public class UpdateMaterialRequest {
    private final String materialCode;
    private final String materialName;
    private final String categoryId;
    private final String unit;
    private final Double currentStock;
    private final Double minStock;
    private final Double unitPrice;
}
