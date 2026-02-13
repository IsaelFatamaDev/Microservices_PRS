package pe.edu.vallegrande.vgmsinventorypurchases.domain.ports.out;

import pe.edu.vallegrande.vgmsinventorypurchases.domain.models.Supplier;
import pe.edu.vallegrande.vgmsinventorypurchases.domain.models.valueobjects.RecordStatus;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ISupplierRepository {
    Mono<Supplier> save(Supplier supplier);

    Mono<Supplier> findById(String id);

    Flux<Supplier> findAll();

    Flux<Supplier> findByRecordStatus(RecordStatus status);

    Mono<Boolean> existsByRuc(String ruc);
}
