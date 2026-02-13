package pe.edu.vallegrande.vgmsinventorypurchases.application.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class PurchaseDetailResponse {
    private final String id;
    private final String purchaseId;
    private final String materialId;
    private final Double quantity;
    private final Double unitPrice;
    private final Double subtotal;
    private final LocalDateTime createdAt;
}
