package pe.edu.vallegrande.vgmsinventorypurchases.domain.ports.in;

import pe.edu.vallegrande.vgmsinventorypurchases.domain.models.Purchase;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface IPurchaseUseCase {
    Mono<Purchase> create(Purchase purchase, String createdBy);

    Mono<Purchase> findById(String id);

    Flux<Purchase> findAll();

    Flux<Purchase> findAllActive();

    Flux<Purchase> findBySupplierId(String supplierId);

    Mono<Purchase> update(String id, Purchase changes, String updatedBy);

    Mono<Void> softDelete(String id, String deletedBy);

    Mono<Purchase> restore(String id, String restoredBy);

    Mono<Purchase> receive(String id, String receivedBy);

    Mono<Purchase> cancel(String id, String cancelledBy);
}
