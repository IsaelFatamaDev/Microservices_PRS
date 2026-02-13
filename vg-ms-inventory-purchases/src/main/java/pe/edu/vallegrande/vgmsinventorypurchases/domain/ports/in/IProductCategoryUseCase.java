package pe.edu.vallegrande.vgmsinventorypurchases.domain.ports.in;

import pe.edu.vallegrande.vgmsinventorypurchases.domain.models.ProductCategory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface IProductCategoryUseCase {
    Mono<ProductCategory> create(ProductCategory category, String createdBy);

    Mono<ProductCategory> findById(String id);

    Flux<ProductCategory> findAll();

    Flux<ProductCategory> findAllActive();

    Mono<ProductCategory> update(String id, ProductCategory changes, String updatedBy);

    Mono<Void> softDelete(String id, String deletedBy);

    Mono<ProductCategory> restore(String id, String restoredBy);
}
