package pe.edu.vallegrande.vgmsinventorypurchases.infrastructure.persistence.adapter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import pe.edu.vallegrande.vgmsinventorypurchases.domain.models.Purchase;
import pe.edu.vallegrande.vgmsinventorypurchases.domain.models.valueobjects.PurchaseStatus;
import pe.edu.vallegrande.vgmsinventorypurchases.domain.models.valueobjects.RecordStatus;
import pe.edu.vallegrande.vgmsinventorypurchases.domain.ports.out.IPurchaseRepository;
import pe.edu.vallegrande.vgmsinventorypurchases.infrastructure.persistence.entity.PurchaseEntity;
import pe.edu.vallegrande.vgmsinventorypurchases.infrastructure.persistence.repository.PurchaseR2dbcRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class PurchaseRepositoryImpl implements IPurchaseRepository {

    private final PurchaseR2dbcRepository r2dbcRepository;

    @Override
    public Mono<Purchase> save(Purchase purchase) {
        PurchaseEntity entity = toEntity(purchase);
        entity.setNew(entity.getId() == null);
        return r2dbcRepository.save(entity).map(this::toDomain);
    }

    @Override
    public Mono<Purchase> findById(String id) {
        return r2dbcRepository.findById(id).map(this::toDomain);
    }

    @Override
    public Flux<Purchase> findAll() {
        return r2dbcRepository.findAll().map(this::toDomain);
    }

    @Override
    public Flux<Purchase> findByRecordStatus(RecordStatus status) {
        return r2dbcRepository.findByRecordStatus(status.name()).map(this::toDomain);
    }

    @Override
    public Flux<Purchase> findBySupplierId(String supplierId) {
        return r2dbcRepository.findBySupplierId(supplierId).map(this::toDomain);
    }

    @Override
    public Mono<Boolean> existsByPurchaseCode(String purchaseCode) {
        return r2dbcRepository.existsByPurchaseCode(purchaseCode);
    }

    private PurchaseEntity toEntity(Purchase domain) {
        return PurchaseEntity.builder()
                .id(domain.getId())
                .organizationId(domain.getOrganizationId())
                .purchaseCode(domain.getPurchaseCode())
                .supplierId(domain.getSupplierId())
                .purchaseDate(domain.getPurchaseDate())
                .totalAmount(domain.getTotalAmount())
                .purchaseStatus(domain.getPurchaseStatus() != null ? domain.getPurchaseStatus().name()
                        : PurchaseStatus.PENDING.name())
                .invoiceNumber(domain.getInvoiceNumber())
                .recordStatus(
                        domain.getRecordStatus() != null ? domain.getRecordStatus().name() : RecordStatus.ACTIVE.name())
                .createdAt(domain.getCreatedAt())
                .createdBy(domain.getCreatedBy())
                .updatedAt(domain.getUpdatedAt())
                .updatedBy(domain.getUpdatedBy())
                .build();
    }

    private Purchase toDomain(PurchaseEntity entity) {
        return Purchase.builder()
                .id(entity.getId())
                .organizationId(entity.getOrganizationId())
                .purchaseCode(entity.getPurchaseCode())
                .supplierId(entity.getSupplierId())
                .purchaseDate(entity.getPurchaseDate())
                .totalAmount(entity.getTotalAmount())
                .purchaseStatus(entity.getPurchaseStatus() != null ? PurchaseStatus.valueOf(entity.getPurchaseStatus())
                        : PurchaseStatus.PENDING)
                .invoiceNumber(entity.getInvoiceNumber())
                .recordStatus(entity.getRecordStatus() != null ? RecordStatus.valueOf(entity.getRecordStatus())
                        : RecordStatus.ACTIVE)
                .createdAt(entity.getCreatedAt())
                .createdBy(entity.getCreatedBy())
                .updatedAt(entity.getUpdatedAt())
                .updatedBy(entity.getUpdatedBy())
                .build();
    }
}
