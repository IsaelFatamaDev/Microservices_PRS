package pe.edu.vallegrande.vgmsinventorypurchases.domain.ports.in;

import pe.edu.vallegrande.vgmsinventorypurchases.domain.models.Supplier;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ISupplierUseCase {
    Mono<Supplier> create(Supplier supplier, String createdBy);

    Mono<Supplier> findById(String id);

    Flux<Supplier> findAll();

    Flux<Supplier> findAllActive();

    Mono<Supplier> update(String id, Supplier changes, String updatedBy);

    Mono<Void> softDelete(String id, String deletedBy);

    Mono<Supplier> restore(String id, String restoredBy);
}
