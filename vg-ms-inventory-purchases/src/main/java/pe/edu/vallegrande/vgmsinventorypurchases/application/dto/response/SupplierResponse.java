package pe.edu.vallegrande.vgmsinventorypurchases.application.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class SupplierResponse {
    private final String id;
    private final String organizationId;
    private final String supplierName;
    private final String ruc;
    private final String address;
    private final String phone;
    private final String email;
    private final String contactPerson;
    private final String recordStatus;
    private final LocalDateTime createdAt;
    private final String createdBy;
    private final LocalDateTime updatedAt;
    private final String updatedBy;
}
