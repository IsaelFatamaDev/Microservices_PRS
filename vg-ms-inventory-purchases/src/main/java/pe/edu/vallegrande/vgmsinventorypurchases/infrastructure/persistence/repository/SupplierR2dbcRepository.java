package pe.edu.vallegrande.vgmsinventorypurchases.infrastructure.persistence.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import pe.edu.vallegrande.vgmsinventorypurchases.infrastructure.persistence.entity.SupplierEntity;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface SupplierR2dbcRepository extends ReactiveCrudRepository<SupplierEntity, String> {
    Flux<SupplierEntity> findByRecordStatus(String recordStatus);

    Mono<Boolean> existsByRuc(String ruc);
}
