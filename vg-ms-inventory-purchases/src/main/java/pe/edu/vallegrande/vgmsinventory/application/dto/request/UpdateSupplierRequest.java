package pe.edu.vallegrande.vgmsinventorypurchases.application.dto.request;

import lombok.Builder;
import lombok.Getter;
import lombok.extern.jackson.Jacksonized;

@Getter
@Builder
@Jacksonized
public class UpdateSupplierRequest {
    private final String supplierName;
    private final String ruc;
    private final String address;
    private final String phone;
    private final String email;
    private final String contactPerson;
}
