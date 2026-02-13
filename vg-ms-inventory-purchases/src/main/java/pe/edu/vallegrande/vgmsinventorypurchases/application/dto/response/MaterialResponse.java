package pe.edu.vallegrande.vgmsinventorypurchases.application.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class MaterialResponse {
    private final String id;
    private final String organizationId;
    private final String materialCode;
    private final String materialName;
    private final String categoryId;
    private final String unit;
    private final Double currentStock;
    private final Double minStock;
    private final Double unitPrice;
    private final String recordStatus;
    private final LocalDateTime createdAt;
    private final String createdBy;
    private final LocalDateTime updatedAt;
    private final String updatedBy;
}
