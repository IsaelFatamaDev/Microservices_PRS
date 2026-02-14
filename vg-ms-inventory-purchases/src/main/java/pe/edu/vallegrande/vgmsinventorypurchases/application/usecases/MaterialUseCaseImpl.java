package pe.edu.vallegrande.vgmsinventorypurchases.application.usecases;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pe.edu.vallegrande.vgmsinventorypurchases.domain.exceptions.ConflictException;
import pe.edu.vallegrande.vgmsinventorypurchases.domain.exceptions.MaterialNotFoundException;
import pe.edu.vallegrande.vgmsinventorypurchases.domain.models.Material;
import pe.edu.vallegrande.vgmsinventorypurchases.domain.models.valueobjects.RecordStatus;
import pe.edu.vallegrande.vgmsinventorypurchases.domain.ports.in.IMaterialUseCase;
import pe.edu.vallegrande.vgmsinventorypurchases.domain.ports.out.IInventoryEventPublisher;
import pe.edu.vallegrande.vgmsinventorypurchases.domain.ports.out.IMaterialRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class MaterialUseCaseImpl implements IMaterialUseCase {

    private final IMaterialRepository repository;
    private final IInventoryEventPublisher eventPublisher;

    @Override
    public Mono<Material> create(Material material, String createdBy) {
        log.info("Creating material: {}", material.getMaterialName());

        // Generar código si no viene
        String materialCode = (material.getMaterialCode() != null && !material.getMaterialCode().trim().isEmpty())
                ? material.getMaterialCode()
                : generateMaterialCode();

        return repository.existsByMaterialCode(materialCode)
                .flatMap(exists -> {
                    if (exists) {
                        return Mono.error(new ConflictException(
                                "Material with code '" + materialCode + "' already exists"));
                    }
                    LocalDateTime now = LocalDateTime.now();
                    Material newMaterial = material.toBuilder()
                            .materialCode(materialCode)
                            .recordStatus(RecordStatus.ACTIVE)
                            .currentStock(material.getCurrentStock() != null ? material.getCurrentStock() : 0.0)
                            .createdAt(now)
                            .createdBy(createdBy)
                            .updatedAt(now)
                            .updatedBy(createdBy)
                            .build();
                    return repository.save(newMaterial);
                })
                .flatMap(saved -> eventPublisher
                        .publishMaterialCreated(saved.getId(), saved.getMaterialCode(), createdBy)
                        .thenReturn(saved))
                .doOnSuccess(saved -> log.info("Material created successfully: {}", saved.getId()))
                .doOnError(error -> log.error("Error creating material: {}", error.getMessage()));
    }

    @Override
    public Mono<Material> findById(String id) {
        log.debug("Finding material by ID: {}", id);
        return repository.findById(id)
                .switchIfEmpty(Mono.error(new MaterialNotFoundException(id)));
    }

    @Override
    public Flux<Material> findAll() {
        log.debug("Finding all materials");
        return repository.findAll();
    }

    @Override
    public Flux<Material> findAllActive() {
        log.debug("Finding all active materials");
        return repository.findByRecordStatus(RecordStatus.ACTIVE);
    }

    @Override
    public Flux<Material> findByCategoryId(String categoryId) {
        log.debug("Finding materials by category: {}", categoryId);
        return repository.findByCategoryId(categoryId);
    }

    @Override
    public Mono<Material> update(String id, Material changes, String updatedBy) {
        log.info("Updating material: {}", id);
        return repository.findById(id)
                .switchIfEmpty(Mono.error(new MaterialNotFoundException(id)))
                .map(existing -> existing.updateWith(changes, updatedBy))
                .flatMap(repository::save)
                .flatMap(saved -> eventPublisher.publishMaterialUpdated(saved.getId(), updatedBy)
                        .thenReturn(saved))
                .doOnSuccess(saved -> log.info("Material updated successfully: {}", saved.getId()))
                .doOnError(error -> log.error("Error updating material {}: {}", id, error.getMessage()));
    }

    @Override
    public Mono<Void> softDelete(String id, String deletedBy) {
        log.info("Soft deleting material: {}", id);
        return repository.findById(id)
                .switchIfEmpty(Mono.error(new MaterialNotFoundException(id)))
                .map(material -> material.markAsDeleted(deletedBy))
                .flatMap(repository::save)
                .flatMap(saved -> eventPublisher.publishMaterialDeleted(saved.getId(), deletedBy)
                        .thenReturn(saved))
                .then()
                .doOnSuccess(v -> log.info("Material soft deleted: {}", id))
                .doOnError(error -> log.error("Error deleting material {}: {}", id, error.getMessage()));
    }

    @Override
    public Mono<Material> restore(String id, String restoredBy) {
        log.info("Restoring material: {}", id);
        return repository.findById(id)
                .switchIfEmpty(Mono.error(new MaterialNotFoundException(id)))
                .map(material -> material.restore(restoredBy))
                .flatMap(repository::save)
                .doOnSuccess(saved -> log.info("Material restored: {}", saved.getId()))
                .doOnError(error -> log.error("Error restoring material {}: {}", id, error.getMessage()));
    }

    private String generateMaterialCode() {
        return "MAT" + System.currentTimeMillis();
    }
}
