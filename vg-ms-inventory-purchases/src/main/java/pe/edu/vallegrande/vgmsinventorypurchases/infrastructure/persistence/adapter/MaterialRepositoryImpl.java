package pe.edu.vallegrande.vgmsinventorypurchases.infrastructure.persistence.adapter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import pe.edu.vallegrande.vgmsinventorypurchases.domain.models.Material;
import pe.edu.vallegrande.vgmsinventorypurchases.domain.models.valueobjects.RecordStatus;
import pe.edu.vallegrande.vgmsinventorypurchases.domain.models.valueobjects.Unit;
import pe.edu.vallegrande.vgmsinventorypurchases.domain.ports.out.IMaterialRepository;
import pe.edu.vallegrande.vgmsinventorypurchases.infrastructure.persistence.entity.MaterialEntity;
import pe.edu.vallegrande.vgmsinventorypurchases.infrastructure.persistence.repository.MaterialR2dbcRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class MaterialRepositoryImpl implements IMaterialRepository {

    private final MaterialR2dbcRepository r2dbcRepository;

    @Override
    public Mono<Material> save(Material material) {
        MaterialEntity entity = toEntity(material);
        entity.setNew(entity.getId() == null);
        return r2dbcRepository.save(entity).map(this::toDomain);
    }

    @Override
    public Mono<Material> findById(String id) {
        return r2dbcRepository.findById(id).map(this::toDomain);
    }

    @Override
    public Flux<Material> findAll() {
        return r2dbcRepository.findAll().map(this::toDomain);
    }

    @Override
    public Flux<Material> findByRecordStatus(RecordStatus status) {
        return r2dbcRepository.findByRecordStatus(status.name()).map(this::toDomain);
    }

    @Override
    public Flux<Material> findByCategoryId(String categoryId) {
        return r2dbcRepository.findByCategoryId(categoryId).map(this::toDomain);
    }

    @Override
    public Mono<Boolean> existsByMaterialCode(String materialCode) {
        return r2dbcRepository.existsByMaterialCode(materialCode);
    }

    private MaterialEntity toEntity(Material domain) {
        return MaterialEntity.builder()
                .id(domain.getId())
                .organizationId(domain.getOrganizationId())
                .materialCode(domain.getMaterialCode())
                .materialName(domain.getMaterialName())
                .categoryId(domain.getCategoryId())
                .unit(domain.getUnit() != null ? domain.getUnit().name() : null)
                .currentStock(domain.getCurrentStock())
                .minStock(domain.getMinStock())
                .unitPrice(domain.getUnitPrice())
                .recordStatus(
                        domain.getRecordStatus() != null ? domain.getRecordStatus().name() : RecordStatus.ACTIVE.name())
                .createdAt(domain.getCreatedAt())
                .createdBy(domain.getCreatedBy())
                .updatedAt(domain.getUpdatedAt())
                .updatedBy(domain.getUpdatedBy())
                .build();
    }

    private Material toDomain(MaterialEntity entity) {
        return Material.builder()
                .id(entity.getId())
                .organizationId(entity.getOrganizationId())
                .materialCode(entity.getMaterialCode())
                .materialName(entity.getMaterialName())
                .categoryId(entity.getCategoryId())
                .unit(entity.getUnit() != null ? Unit.valueOf(entity.getUnit()) : null)
                .currentStock(entity.getCurrentStock())
                .minStock(entity.getMinStock())
                .unitPrice(entity.getUnitPrice())
                .recordStatus(entity.getRecordStatus() != null ? RecordStatus.valueOf(entity.getRecordStatus())
                        : RecordStatus.ACTIVE)
                .createdAt(entity.getCreatedAt())
                .createdBy(entity.getCreatedBy())
                .updatedAt(entity.getUpdatedAt())
                .updatedBy(entity.getUpdatedBy())
                .build();
    }
}
