package pe.edu.vallegrande.vgmsinventorypurchases.domain.ports.out;

import pe.edu.vallegrande.vgmsinventorypurchases.domain.models.PurchaseDetail;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface IPurchaseDetailRepository {
    Mono<PurchaseDetail> save(PurchaseDetail detail);

    Flux<PurchaseDetail> findByPurchaseId(String purchaseId);

    Mono<Void> deleteByPurchaseId(String purchaseId);
}
