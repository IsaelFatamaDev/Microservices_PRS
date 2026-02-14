package pe.edu.vallegrande.vgmsinventorypurchases.infrastructure.persistence.adapter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import pe.edu.vallegrande.vgmsinventorypurchases.domain.models.Supplier;
import pe.edu.vallegrande.vgmsinventorypurchases.domain.models.valueobjects.RecordStatus;
import pe.edu.vallegrande.vgmsinventorypurchases.domain.ports.out.ISupplierRepository;
import pe.edu.vallegrande.vgmsinventorypurchases.infrastructure.persistence.entity.SupplierEntity;
import pe.edu.vallegrande.vgmsinventorypurchases.infrastructure.persistence.repository.SupplierR2dbcRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class SupplierRepositoryImpl implements ISupplierRepository {

    private final SupplierR2dbcRepository r2dbcRepository;

    @Override
    public Mono<Supplier> save(Supplier supplier) {
        SupplierEntity entity = toEntity(supplier);
        entity.setNew(entity.getId() == null);
        return r2dbcRepository.save(entity).map(this::toDomain);
    }

    @Override
    public Mono<Supplier> findById(String id) {
        return r2dbcRepository.findById(id).map(this::toDomain);
    }

    @Override
    public Flux<Supplier> findAll() {
        return r2dbcRepository.findAll().map(this::toDomain);
    }

    @Override
    public Flux<Supplier> findByRecordStatus(RecordStatus status) {
        return r2dbcRepository.findByRecordStatus(status.name()).map(this::toDomain);
    }

    @Override
    public Mono<Boolean> existsByRuc(String ruc) {
        return r2dbcRepository.existsByRuc(ruc);
    }

    private SupplierEntity toEntity(Supplier domain) {
        return SupplierEntity.builder()
                .id(domain.getId())
                .organizationId(domain.getOrganizationId())
                .supplierName(domain.getSupplierName())
                .ruc(domain.getRuc())
                .address(domain.getAddress())
                .phone(domain.getPhone())
                .email(domain.getEmail())
                .contactPerson(domain.getContactPerson())
                .recordStatus(
                        domain.getRecordStatus() != null ? domain.getRecordStatus().name() : RecordStatus.ACTIVE.name())
                .createdAt(domain.getCreatedAt())
                .createdBy(domain.getCreatedBy())
                .updatedAt(domain.getUpdatedAt())
                .updatedBy(domain.getUpdatedBy())
                .build();
    }

    private Supplier toDomain(SupplierEntity entity) {
        return Supplier.builder()
                .id(entity.getId())
                .organizationId(entity.getOrganizationId())
                .supplierName(entity.getSupplierName())
                .ruc(entity.getRuc())
                .address(entity.getAddress())
                .phone(entity.getPhone())
                .email(entity.getEmail())
                .contactPerson(entity.getContactPerson())
                .recordStatus(entity.getRecordStatus() != null ? RecordStatus.valueOf(entity.getRecordStatus())
                        : RecordStatus.ACTIVE)
                .createdAt(entity.getCreatedAt())
                .createdBy(entity.getCreatedBy())
                .updatedAt(entity.getUpdatedAt())
                .updatedBy(entity.getUpdatedBy())
                .build();
    }
}
