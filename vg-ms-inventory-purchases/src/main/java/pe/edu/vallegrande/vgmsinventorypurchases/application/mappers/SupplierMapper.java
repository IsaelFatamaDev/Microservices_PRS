package pe.edu.vallegrande.vgmsinventorypurchases.application.mappers;

import org.springframework.stereotype.Component;
import pe.edu.vallegrande.vgmsinventorypurchases.application.dto.request.CreateSupplierRequest;
import pe.edu.vallegrande.vgmsinventorypurchases.application.dto.request.UpdateSupplierRequest;
import pe.edu.vallegrande.vgmsinventorypurchases.application.dto.response.SupplierResponse;
import pe.edu.vallegrande.vgmsinventorypurchases.domain.models.Supplier;

@Component
public class SupplierMapper {

    public Supplier toDomain(CreateSupplierRequest request) {
        return Supplier.builder()
                .organizationId(request.getOrganizationId())
                .supplierName(request.getSupplierName())
                .ruc(request.getRuc())
                .address(request.getAddress())
                .phone(request.getPhone())
                .email(request.getEmail())
                .contactPerson(request.getContactPerson())
                .build();
    }

    public Supplier toDomain(UpdateSupplierRequest request) {
        return Supplier.builder()
                .supplierName(request.getSupplierName())
                .ruc(request.getRuc())
                .address(request.getAddress())
                .phone(request.getPhone())
                .email(request.getEmail())
                .contactPerson(request.getContactPerson())
                .build();
    }

    public SupplierResponse toResponse(Supplier domain) {
        return SupplierResponse.builder()
                .id(domain.getId())
                .organizationId(domain.getOrganizationId())
                .supplierName(domain.getSupplierName())
                .ruc(domain.getRuc())
                .address(domain.getAddress())
                .phone(domain.getPhone())
                .email(domain.getEmail())
                .contactPerson(domain.getContactPerson())
                .recordStatus(domain.getRecordStatus() != null ? domain.getRecordStatus().name() : null)
                .createdAt(domain.getCreatedAt())
                .createdBy(domain.getCreatedBy())
                .updatedAt(domain.getUpdatedAt())
                .updatedBy(domain.getUpdatedBy())
                .build();
    }
}
