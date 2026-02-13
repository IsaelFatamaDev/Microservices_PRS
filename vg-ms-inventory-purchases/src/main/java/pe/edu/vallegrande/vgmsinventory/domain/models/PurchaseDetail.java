package pe.edu.vallegrande.vgmsinventorypurchases.domain.models;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder(toBuilder = true)
public class PurchaseDetail {
    private String id;
    private String purchaseId;
    private String materialId;
    private Double quantity;
    private Double unitPrice;
    private Double subtotal;
    private LocalDateTime createdAt;

    public static PurchaseDetail create(String purchaseId, String materialId, Double quantity, Double unitPrice) {
        return PurchaseDetail.builder()
                .purchaseId(purchaseId)
                .materialId(materialId)
                .quantity(quantity)
                .unitPrice(unitPrice)
                .subtotal(quantity * unitPrice)
                .createdAt(LocalDateTime.now())
                .build();
    }
}
