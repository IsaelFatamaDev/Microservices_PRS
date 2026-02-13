package pe.edu.vallegrande.vgmsinventorypurchases.application.mappers;

import org.springframework.stereotype.Component;
import pe.edu.vallegrande.vgmsinventorypurchases.application.dto.request.CreatePurchaseRequest;
import pe.edu.vallegrande.vgmsinventorypurchases.application.dto.response.PurchaseDetailResponse;
import pe.edu.vallegrande.vgmsinventorypurchases.application.dto.response.PurchaseResponse;
import pe.edu.vallegrande.vgmsinventorypurchases.domain.models.Purchase;
import pe.edu.vallegrande.vgmsinventorypurchases.domain.models.PurchaseDetail;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class PurchaseMapper {

    public Purchase toDomain(CreatePurchaseRequest request) {
        List<PurchaseDetail> details = request.getDetails() != null
                ? request.getDetails().stream()
                        .map(d -> PurchaseDetail.builder()
                                .materialId(d.getMaterialId())
                                .quantity(d.getQuantity())
                                .unitPrice(d.getUnitPrice())
                                .subtotal(d.getQuantity() * d.getUnitPrice())
                                .build())
                        .collect(Collectors.toList())
                : Collections.emptyList();

        return Purchase.builder()
                .organizationId(request.getOrganizationId())
                .purchaseCode(request.getPurchaseCode())
                .supplierId(request.getSupplierId())
                .purchaseDate(request.getPurchaseDate() != null ? LocalDateTime.parse(request.getPurchaseDate())
                        : LocalDateTime.now())
                .totalAmount(request.getTotalAmount())
                .invoiceNumber(request.getInvoiceNumber())
                .details(details)
                .build();
    }

    public PurchaseResponse toResponse(Purchase domain) {
        List<PurchaseDetailResponse> detailResponses = domain.getDetails() != null
                ? domain.getDetails().stream()
                        .map(this::toDetailResponse)
                        .collect(Collectors.toList())
                : Collections.emptyList();

        return PurchaseResponse.builder()
                .id(domain.getId())
                .organizationId(domain.getOrganizationId())
                .purchaseCode(domain.getPurchaseCode())
                .supplierId(domain.getSupplierId())
                .purchaseDate(domain.getPurchaseDate())
                .totalAmount(domain.getTotalAmount())
                .purchaseStatus(domain.getPurchaseStatus() != null ? domain.getPurchaseStatus().name() : null)
                .invoiceNumber(domain.getInvoiceNumber())
                .details(detailResponses)
                .recordStatus(domain.getRecordStatus() != null ? domain.getRecordStatus().name() : null)
                .createdAt(domain.getCreatedAt())
                .createdBy(domain.getCreatedBy())
                .updatedAt(domain.getUpdatedAt())
                .updatedBy(domain.getUpdatedBy())
                .build();
    }

    public PurchaseDetailResponse toDetailResponse(PurchaseDetail detail) {
        return PurchaseDetailResponse.builder()
                .id(detail.getId())
                .purchaseId(detail.getPurchaseId())
                .materialId(detail.getMaterialId())
                .quantity(detail.getQuantity())
                .unitPrice(detail.getUnitPrice())
                .subtotal(detail.getSubtotal())
                .createdAt(detail.getCreatedAt())
                .build();
    }
}
