package pe.edu.vallegrande.vgmsinventorypurchases.application.usecases;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pe.edu.vallegrande.vgmsinventorypurchases.domain.exceptions.ConflictException;
import pe.edu.vallegrande.vgmsinventorypurchases.domain.exceptions.SupplierNotFoundException;
import pe.edu.vallegrande.vgmsinventorypurchases.domain.models.Supplier;
import pe.edu.vallegrande.vgmsinventorypurchases.domain.models.valueobjects.RecordStatus;
import pe.edu.vallegrande.vgmsinventorypurchases.domain.ports.in.ISupplierUseCase;
import pe.edu.vallegrande.vgmsinventorypurchases.domain.ports.out.IInventoryEventPublisher;
import pe.edu.vallegrande.vgmsinventorypurchases.domain.ports.out.ISupplierRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class SupplierUseCaseImpl implements ISupplierUseCase {

    private final ISupplierRepository repository;
    private final IInventoryEventPublisher eventPublisher;

    @Override
    public Mono<Supplier> create(Supplier supplier, String createdBy) {
        log.info("Creating supplier: {}", supplier.getSupplierName());

        // Solo validar RUC duplicado si el RUC no es null/empty
        Mono<Boolean> validateRuc = (supplier.getRuc() != null && !supplier.getRuc().trim().isEmpty())
                ? repository.existsByRuc(supplier.getRuc())
                : Mono.just(false);

        return validateRuc.flatMap(exists -> {
            if (exists) {
                return Mono.error(
                        new ConflictException("Supplier with RUC '" + supplier.getRuc() + "' already exists"));
            }
            LocalDateTime now = LocalDateTime.now();
            Supplier newSupplier = supplier.toBuilder()
                    .recordStatus(RecordStatus.ACTIVE)
                    .createdAt(now)
                    .createdBy(createdBy)
                    .updatedAt(now)
                    .updatedBy(createdBy)
                    .build();
            return repository.save(newSupplier);
        })
                .doOnSuccess(saved -> log.info("Supplier created successfully: {}", saved.getId()))
                .doOnError(error -> log.error("Error creating supplier: {}", error.getMessage()));
    }

    @Override
    public Mono<Supplier> findById(String id) {
        log.debug("Finding supplier by ID: {}", id);
        return repository.findById(id)
                .switchIfEmpty(Mono.error(new SupplierNotFoundException(id)));
    }

    @Override
    public Flux<Supplier> findAll() {
        log.debug("Finding all suppliers");
        return repository.findAll();
    }

    @Override
    public Flux<Supplier> findAllActive() {
        log.debug("Finding all active suppliers");
        return repository.findByRecordStatus(RecordStatus.ACTIVE);
    }

    @Override
    public Mono<Supplier> update(String id, Supplier changes, String updatedBy) {
        log.info("Updating supplier: {}", id);
        return repository.findById(id)
                .switchIfEmpty(Mono.error(new SupplierNotFoundException(id)))
                .map(existing -> existing.updateWith(changes, updatedBy))
                .flatMap(repository::save)
                .doOnSuccess(saved -> log.info("Supplier updated successfully: {}", saved.getId()))
                .doOnError(error -> log.error("Error updating supplier {}: {}", id, error.getMessage()));
    }

    @Override
    public Mono<Void> softDelete(String id, String deletedBy) {
        log.info("Soft deleting supplier: {}", id);
        return repository.findById(id)
                .switchIfEmpty(Mono.error(new SupplierNotFoundException(id)))
                .map(supplier -> supplier.markAsDeleted(deletedBy))
                .flatMap(repository::save)
                .then()
                .doOnSuccess(v -> log.info("Supplier soft deleted: {}", id))
                .doOnError(error -> log.error("Error deleting supplier {}: {}", id, error.getMessage()));
    }

    @Override
    public Mono<Supplier> restore(String id, String restoredBy) {
        log.info("Restoring supplier: {}", id);
        return repository.findById(id)
                .switchIfEmpty(Mono.error(new SupplierNotFoundException(id)))
                .map(supplier -> supplier.restore(restoredBy))
                .flatMap(repository::save)
                .doOnSuccess(saved -> log.info("Supplier restored: {}", saved.getId()))
                .doOnError(error -> log.error("Error restoring supplier {}: {}", id, error.getMessage()));
    }
}
