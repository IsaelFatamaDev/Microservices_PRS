package pe.edu.vallegrande.vgmsinventorypurchases.application.mappers;

import org.springframework.stereotype.Component;
import pe.edu.vallegrande.vgmsinventorypurchases.application.dto.request.CreateProductCategoryRequest;
import pe.edu.vallegrande.vgmsinventorypurchases.application.dto.request.UpdateProductCategoryRequest;
import pe.edu.vallegrande.vgmsinventorypurchases.application.dto.response.ProductCategoryResponse;
import pe.edu.vallegrande.vgmsinventorypurchases.domain.models.ProductCategory;

@Component
public class ProductCategoryMapper {

    public ProductCategory toDomain(CreateProductCategoryRequest request) {
        return ProductCategory.builder()
                .organizationId(request.getOrganizationId())
                .categoryName(request.getCategoryName())
                .description(request.getDescription())
                .build();
    }

    public ProductCategory toDomain(UpdateProductCategoryRequest request) {
        return ProductCategory.builder()
                .categoryName(request.getCategoryName())
                .description(request.getDescription())
                .build();
    }

    public ProductCategoryResponse toResponse(ProductCategory domain) {
        return ProductCategoryResponse.builder()
                .id(domain.getId())
                .organizationId(domain.getOrganizationId())
                .categoryName(domain.getCategoryName())
                .description(domain.getDescription())
                .recordStatus(domain.getRecordStatus() != null ? domain.getRecordStatus().name() : null)
                .createdAt(domain.getCreatedAt())
                .createdBy(domain.getCreatedBy())
                .updatedAt(domain.getUpdatedAt())
                .updatedBy(domain.getUpdatedBy())
                .build();
    }
}
