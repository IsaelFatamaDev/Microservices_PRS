package pe.edu.vallegrande.vgmscommercial.domain.ports.in.debt;

import pe.edu.vallegrande.vgmscommercial.domain.models.Debt;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface IGetDebtUseCase {
    Mono<Debt> findById(String id, String organizationId);
    Flux<Debt> findAll(String organizationId, String status, String userId, Integer page, Integer size);
    Flux<Debt> findByUserId(String userId, String organizationId);
    Flux<Debt> findPendingDebts(String organizationId);
    Mono<Double> getTotalDebtByUser(String userId, String organizationId);
    Mono<Long> count(String organizationId, String status);
}