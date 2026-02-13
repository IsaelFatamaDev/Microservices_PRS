package pe.edu.vallegrande.vgmscommercial.domain.ports.out;

import pe.edu.vallegrande.vgmscommercial.domain.models.PettyCash;
import pe.edu.vallegrande.vgmscommercial.domain.models.PettyCashMovement;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface IPettyCashRepository {
    Mono<PettyCash> save(PettyCash pettyCash);
    Mono<PettyCash> findById(String id);
    Flux<PettyCash> findByOrganizationId(String organizationId);
    Mono<PettyCash> findActiveByOrganizationId(String organizationId);
    Mono<PettyCashMovement> saveMovement(PettyCashMovement movement);
    Flux<PettyCashMovement> findMovementsByPettyCashId(String pettyCashId);
    Mono<Long> countByOrganizationIdAndStatus(String organizationId, String status);
}