package pe.edu.vallegrande.vgmsinventorypurchases.domain.ports.in;

import pe.edu.vallegrande.vgmsinventorypurchases.domain.models.Material;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface IMaterialUseCase {
    Mono<Material> create(Material material, String createdBy);

    Mono<Material> findById(String id);

    Flux<Material> findAll();

    Flux<Material> findAllActive();

    Flux<Material> findByCategoryId(String categoryId);

    Mono<Material> update(String id, Material changes, String updatedBy);

    Mono<Void> softDelete(String id, String deletedBy);

    Mono<Material> restore(String id, String restoredBy);
}
