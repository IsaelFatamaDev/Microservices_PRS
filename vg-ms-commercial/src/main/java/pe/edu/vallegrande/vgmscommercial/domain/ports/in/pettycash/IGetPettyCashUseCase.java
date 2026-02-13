package pe.edu.vallegrande.vgmscommercial.domain.ports.in.pettycash;

import pe.edu.vallegrande.vgmscommercial.domain.models.PettyCash;
import pe.edu.vallegrande.vgmscommercial.domain.models.PettyCashMovement;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface IGetPettyCashUseCase {
    Mono<PettyCash> findById(String id, String organizationId);
    Flux<PettyCash> findAll(String organizationId, String status, Integer page, Integer size);
    Mono<PettyCash> findActiveByOrganization(String organizationId);
    Flux<PettyCashMovement> findMovements(String pettyCashId, String organizationId);
    Mono<Long> count(String organizationId, String status);
}