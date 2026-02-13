package pe.edu.vallegrande.vgmsinventorypurchases.infrastructure.persistence.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import pe.edu.vallegrande.vgmsinventorypurchases.infrastructure.persistence.entity.PurchaseEntity;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface PurchaseR2dbcRepository extends ReactiveCrudRepository<PurchaseEntity, String> {
    Flux<PurchaseEntity> findByRecordStatus(String recordStatus);

    Flux<PurchaseEntity> findBySupplierId(String supplierId);

    Mono<Boolean> existsByPurchaseCode(String purchaseCode);
}
