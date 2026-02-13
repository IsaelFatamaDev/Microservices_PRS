package pe.edu.vallegrande.vgmsinventorypurchases.infrastructure.persistence.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import pe.edu.vallegrande.vgmsinventorypurchases.infrastructure.persistence.entity.ProductCategoryEntity;
import reactor.core.publisher.Flux;

@Repository
public interface ProductCategoryR2dbcRepository extends ReactiveCrudRepository<ProductCategoryEntity, String> {
    Flux<ProductCategoryEntity> findByRecordStatus(String recordStatus);
}
