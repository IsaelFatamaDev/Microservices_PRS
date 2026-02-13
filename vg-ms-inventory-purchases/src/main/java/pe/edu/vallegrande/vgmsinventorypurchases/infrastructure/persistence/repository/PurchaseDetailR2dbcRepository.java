package pe.edu.vallegrande.vgmsinventorypurchases.infrastructure.persistence.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import pe.edu.vallegrande.vgmsinventorypurchases.infrastructure.persistence.entity.PurchaseDetailEntity;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface PurchaseDetailR2dbcRepository extends ReactiveCrudRepository<PurchaseDetailEntity, String> {
    Flux<PurchaseDetailEntity> findByPurchaseId(String purchaseId);

    Mono<Void> deleteByPurchaseId(String purchaseId);
}
