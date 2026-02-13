package pe.edu.vallegrande.vgmsinventorypurchases.domain.ports.out;

import pe.edu.vallegrande.vgmsinventorypurchases.domain.models.Material;
import pe.edu.vallegrande.vgmsinventorypurchases.domain.models.valueobjects.RecordStatus;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface IMaterialRepository {
    Mono<Material> save(Material material);

    Mono<Material> findById(String id);

    Flux<Material> findAll();

    Flux<Material> findByRecordStatus(RecordStatus status);

    Flux<Material> findByCategoryId(String categoryId);

    Mono<Boolean> existsByMaterialCode(String materialCode);
}
