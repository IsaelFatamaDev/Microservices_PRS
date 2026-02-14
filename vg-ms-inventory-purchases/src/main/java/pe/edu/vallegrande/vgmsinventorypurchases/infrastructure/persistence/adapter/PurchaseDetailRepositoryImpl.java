package pe.edu.vallegrande.vgmsinventorypurchases.infrastructure.persistence.adapter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import pe.edu.vallegrande.vgmsinventorypurchases.domain.models.PurchaseDetail;
import pe.edu.vallegrande.vgmsinventorypurchases.domain.ports.out.IPurchaseDetailRepository;
import pe.edu.vallegrande.vgmsinventorypurchases.infrastructure.persistence.entity.PurchaseDetailEntity;
import pe.edu.vallegrande.vgmsinventorypurchases.infrastructure.persistence.repository.PurchaseDetailR2dbcRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class PurchaseDetailRepositoryImpl implements IPurchaseDetailRepository {

    private final PurchaseDetailR2dbcRepository r2dbcRepository;

    @Override
    public Mono<PurchaseDetail> save(PurchaseDetail detail) {
        PurchaseDetailEntity entity = toEntity(detail);
        entity.setNew(entity.getId() == null);
        return r2dbcRepository.save(entity).map(this::toDomain);
    }

    @Override
    public Flux<PurchaseDetail> findByPurchaseId(String purchaseId) {
        return r2dbcRepository.findByPurchaseId(purchaseId).map(this::toDomain);
    }

    @Override
    public Mono<Void> deleteByPurchaseId(String purchaseId) {
        return r2dbcRepository.deleteByPurchaseId(purchaseId);
    }

    private PurchaseDetailEntity toEntity(PurchaseDetail domain) {
        return PurchaseDetailEntity.builder()
                .id(domain.getId())
                .purchaseId(domain.getPurchaseId())
                .materialId(domain.getMaterialId())
                .quantity(domain.getQuantity())
                .unitPrice(domain.getUnitPrice())
                .subtotal(domain.getSubtotal())
                .createdAt(domain.getCreatedAt())
                .build();
    }

    private PurchaseDetail toDomain(PurchaseDetailEntity entity) {
        return PurchaseDetail.builder()
                .id(entity.getId())
                .purchaseId(entity.getPurchaseId())
                .materialId(entity.getMaterialId())
                .quantity(entity.getQuantity())
                .unitPrice(entity.getUnitPrice())
                .subtotal(entity.getSubtotal())
                .createdAt(entity.getCreatedAt())
                .build();
    }
}
