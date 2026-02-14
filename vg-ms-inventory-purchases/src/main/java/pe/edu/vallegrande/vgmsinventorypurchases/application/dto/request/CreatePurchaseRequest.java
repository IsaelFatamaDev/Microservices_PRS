package pe.edu.vallegrande.vgmsinventorypurchases.application.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.jackson.Jacksonized;

import java.util.List;

@Getter
@Builder
@Jacksonized
public class CreatePurchaseRequest {
    @NotBlank(message = "Organization ID is required")
    private final String organizationId;
    private final String purchaseCode;
    @NotBlank(message = "Supplier ID is required")
    private final String supplierId;
    private final String purchaseDate;
    private final Double totalAmount;
    private final String invoiceNumber;
    @Valid
    private final List<PurchaseDetailRequest> details;

    @Getter
    @Builder
    @Jacksonized
    public static class PurchaseDetailRequest {
        @NotBlank(message = "Material ID is required")
        private final String materialId;
        @NotNull(message = "Quantity is required")
        private final Double quantity;
        @NotNull(message = "Unit price is required")
        private final Double unitPrice;
    }
}
