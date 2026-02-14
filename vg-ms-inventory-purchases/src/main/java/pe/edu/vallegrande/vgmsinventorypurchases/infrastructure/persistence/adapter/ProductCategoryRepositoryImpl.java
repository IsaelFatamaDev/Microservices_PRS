package pe.edu.vallegrande.vgmsinventorypurchases.infrastructure.persistence.adapter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import pe.edu.vallegrande.vgmsinventorypurchases.domain.models.ProductCategory;
import pe.edu.vallegrande.vgmsinventorypurchases.domain.models.valueobjects.RecordStatus;
import pe.edu.vallegrande.vgmsinventorypurchases.domain.ports.out.IProductCategoryRepository;
import pe.edu.vallegrande.vgmsinventorypurchases.infrastructure.persistence.entity.ProductCategoryEntity;
import pe.edu.vallegrande.vgmsinventorypurchases.infrastructure.persistence.repository.ProductCategoryR2dbcRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class ProductCategoryRepositoryImpl implements IProductCategoryRepository {

    private final ProductCategoryR2dbcRepository r2dbcRepository;

    @Override
    public Mono<ProductCategory> save(ProductCategory category) {
        ProductCategoryEntity entity = toEntity(category);
        // Marcar como nueva si no tiene ID para que Spring Data haga INSERT
        entity.setNew(entity.getId() == null);
        return r2dbcRepository.save(entity).map(this::toDomain);
    }

    @Override
    public Mono<ProductCategory> findById(String id) {
        return r2dbcRepository.findById(id).map(this::toDomain);
    }

    @Override
    public Flux<ProductCategory> findAll() {
        return r2dbcRepository.findAll().map(this::toDomain);
    }

    @Override
    public Flux<ProductCategory> findByRecordStatus(RecordStatus status) {
        return r2dbcRepository.findByRecordStatus(status.name()).map(this::toDomain);
    }

    private ProductCategoryEntity toEntity(ProductCategory domain) {
        return ProductCategoryEntity.builder()
                .id(domain.getId())
                .organizationId(domain.getOrganizationId())
                .categoryName(domain.getCategoryName())
                .description(domain.getDescription())
                .recordStatus(
                        domain.getRecordStatus() != null ? domain.getRecordStatus().name() : RecordStatus.ACTIVE.name())
                .createdAt(domain.getCreatedAt())
                .createdBy(domain.getCreatedBy())
                .updatedAt(domain.getUpdatedAt())
                .updatedBy(domain.getUpdatedBy())
                .build();
    }

    private ProductCategory toDomain(ProductCategoryEntity entity) {
        return ProductCategory.builder()
                .id(entity.getId())
                .organizationId(entity.getOrganizationId())
                .categoryName(entity.getCategoryName())
                .description(entity.getDescription())
                .recordStatus(entity.getRecordStatus() != null ? RecordStatus.valueOf(entity.getRecordStatus())
                        : RecordStatus.ACTIVE)
                .createdAt(entity.getCreatedAt())
                .createdBy(entity.getCreatedBy())
                .updatedAt(entity.getUpdatedAt())
                .updatedBy(entity.getUpdatedBy())
                .build();
    }
}
