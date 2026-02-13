package pe.edu.vallegrande.vgmsinventorypurchases.application.mappers;

import org.springframework.stereotype.Component;
import pe.edu.vallegrande.vgmsinventorypurchases.application.dto.request.CreateMaterialRequest;
import pe.edu.vallegrande.vgmsinventorypurchases.application.dto.request.UpdateMaterialRequest;
import pe.edu.vallegrande.vgmsinventorypurchases.application.dto.response.MaterialResponse;
import pe.edu.vallegrande.vgmsinventorypurchases.domain.models.Material;
import pe.edu.vallegrande.vgmsinventorypurchases.domain.models.valueobjects.Unit;

@Component
public class MaterialMapper {

    public Material toDomain(CreateMaterialRequest request) {
        return Material.builder()
                .organizationId(request.getOrganizationId())
                .materialCode(request.getMaterialCode())
                .materialName(request.getMaterialName())
                .categoryId(request.getCategoryId())
                .unit(Unit.valueOf(request.getUnit().toUpperCase()))
                .currentStock(request.getCurrentStock())
                .minStock(request.getMinStock())
                .unitPrice(request.getUnitPrice())
                .build();
    }

    public Material toDomain(UpdateMaterialRequest request) {
        return Material.builder()
                .materialCode(request.getMaterialCode())
                .materialName(request.getMaterialName())
                .categoryId(request.getCategoryId())
                .unit(request.getUnit() != null ? Unit.valueOf(request.getUnit().toUpperCase()) : null)
                .currentStock(request.getCurrentStock())
                .minStock(request.getMinStock())
                .unitPrice(request.getUnitPrice())
                .build();
    }

    public MaterialResponse toResponse(Material domain) {
        return MaterialResponse.builder()
                .id(domain.getId())
                .organizationId(domain.getOrganizationId())
                .materialCode(domain.getMaterialCode())
                .materialName(domain.getMaterialName())
                .categoryId(domain.getCategoryId())
                .unit(domain.getUnit() != null ? domain.getUnit().name() : null)
                .currentStock(domain.getCurrentStock())
                .minStock(domain.getMinStock())
                .unitPrice(domain.getUnitPrice())
                .recordStatus(domain.getRecordStatus() != null ? domain.getRecordStatus().name() : null)
                .createdAt(domain.getCreatedAt())
                .createdBy(domain.getCreatedBy())
                .updatedAt(domain.getUpdatedAt())
                .updatedBy(domain.getUpdatedBy())
                .build();
    }
}
