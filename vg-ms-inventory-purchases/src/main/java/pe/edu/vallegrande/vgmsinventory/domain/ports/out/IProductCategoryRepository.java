package pe.edu.vallegrande.vgmsinventorypurchases.domain.ports.out;

import pe.edu.vallegrande.vgmsinventorypurchases.domain.models.ProductCategory;
import pe.edu.vallegrande.vgmsinventorypurchases.domain.models.valueobjects.RecordStatus;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface IProductCategoryRepository {
    Mono<ProductCategory> save(ProductCategory category);

    Mono<ProductCategory> findById(String id);

    Flux<ProductCategory> findAll();

    Flux<ProductCategory> findByRecordStatus(RecordStatus status);
}
