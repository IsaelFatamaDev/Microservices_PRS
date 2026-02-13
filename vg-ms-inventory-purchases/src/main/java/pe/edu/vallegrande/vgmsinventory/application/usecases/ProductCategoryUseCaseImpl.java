package pe.edu.vallegrande.vgmsinventorypurchases.application.usecases;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pe.edu.vallegrande.vgmsinventorypurchases.domain.exceptions.CategoryNotFoundException;
import pe.edu.vallegrande.vgmsinventorypurchases.domain.models.ProductCategory;
import pe.edu.vallegrande.vgmsinventorypurchases.domain.models.valueobjects.RecordStatus;
import pe.edu.vallegrande.vgmsinventorypurchases.domain.ports.in.IProductCategoryUseCase;
import pe.edu.vallegrande.vgmsinventorypurchases.domain.ports.out.IProductCategoryRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductCategoryUseCaseImpl implements IProductCategoryUseCase {

    private final IProductCategoryRepository repository;

    @Override
    public Mono<ProductCategory> create(ProductCategory category, String createdBy) {
        log.info("Creating product category: {}", category.getCategoryName());
        LocalDateTime now = LocalDateTime.now();
        ProductCategory newCategory = category.toBuilder()
                .recordStatus(RecordStatus.ACTIVE)
                .createdAt(now)
                .createdBy(createdBy)
                .updatedAt(now)
                .updatedBy(createdBy)
                .build();
        return repository.save(newCategory)
                .doOnSuccess(saved -> log.info("Product category created successfully: {}", saved.getId()))
                .doOnError(error -> log.error("Error creating product category: {}", error.getMessage()));
    }

    @Override
    public Mono<ProductCategory> findById(String id) {
        log.debug("Finding product category by ID: {}", id);
        return repository.findById(id)
                .switchIfEmpty(Mono.error(new CategoryNotFoundException(id)));
    }

    @Override
    public Flux<ProductCategory> findAll() {
        log.debug("Finding all product categories");
        return repository.findAll();
    }

    @Override
    public Flux<ProductCategory> findAllActive() {
        log.debug("Finding all active product categories");
        return repository.findByRecordStatus(RecordStatus.ACTIVE);
    }

    @Override
    public Mono<ProductCategory> update(String id, ProductCategory changes, String updatedBy) {
        log.info("Updating product category: {}", id);
        return repository.findById(id)
                .switchIfEmpty(Mono.error(new CategoryNotFoundException(id)))
                .map(existing -> existing.updateWith(changes, updatedBy))
                .flatMap(repository::save)
                .doOnSuccess(saved -> log.info("Product category updated successfully: {}", saved.getId()))
                .doOnError(error -> log.error("Error updating product category {}: {}", id, error.getMessage()));
    }

    @Override
    public Mono<Void> softDelete(String id, String deletedBy) {
        log.info("Soft deleting product category: {}", id);
        return repository.findById(id)
                .switchIfEmpty(Mono.error(new CategoryNotFoundException(id)))
                .map(category -> category.markAsDeleted(deletedBy))
                .flatMap(repository::save)
                .then()
                .doOnSuccess(v -> log.info("Product category soft deleted: {}", id))
                .doOnError(error -> log.error("Error deleting product category {}: {}", id, error.getMessage()));
    }

    @Override
    public Mono<ProductCategory> restore(String id, String restoredBy) {
        log.info("Restoring product category: {}", id);
        return repository.findById(id)
                .switchIfEmpty(Mono.error(new CategoryNotFoundException(id)))
                .map(category -> category.restore(restoredBy))
                .flatMap(repository::save)
                .doOnSuccess(saved -> log.info("Product category restored: {}", saved.getId()))
                .doOnError(error -> log.error("Error restoring product category {}: {}", id, error.getMessage()));
    }
}
