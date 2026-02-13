package pe.edu.vallegrande.vgmsinventorypurchases.domain.ports.out;

import pe.edu.vallegrande.vgmsinventorypurchases.domain.models.Purchase;
import pe.edu.vallegrande.vgmsinventorypurchases.domain.models.valueobjects.RecordStatus;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface IPurchaseRepository {
    Mono<Purchase> save(Purchase purchase);

    Mono<Purchase> findById(String id);

    Flux<Purchase> findAll();

    Flux<Purchase> findByRecordStatus(RecordStatus status);

    Flux<Purchase> findBySupplierId(String supplierId);

    Mono<Boolean> existsByPurchaseCode(String purchaseCode);
}
