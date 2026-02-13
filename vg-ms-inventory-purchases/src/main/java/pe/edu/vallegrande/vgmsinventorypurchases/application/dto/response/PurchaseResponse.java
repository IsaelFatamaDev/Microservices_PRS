package pe.edu.vallegrande.vgmsinventorypurchases.application.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class PurchaseResponse {
    private final String id;
    private final String organizationId;
    private final String purchaseCode;
    private final String supplierId;
    private final LocalDateTime purchaseDate;
    private final Double totalAmount;
    private final String purchaseStatus;
    private final String invoiceNumber;
    private final List<PurchaseDetailResponse> details;
    private final String recordStatus;
    private final LocalDateTime createdAt;
    private final String createdBy;
    private final LocalDateTime updatedAt;
    private final String updatedBy;
}
