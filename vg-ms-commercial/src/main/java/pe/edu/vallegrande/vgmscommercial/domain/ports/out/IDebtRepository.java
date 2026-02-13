package pe.edu.vallegrande.vgmscommercial.domain.ports.out;

import pe.edu.vallegrande.vgmscommercial.domain.models.Debt;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface IDebtRepository {
    Mono<Debt> save(Debt debt);
    Mono<Debt> findById(String id);
    Flux<Debt> findByOrganizationId(String organizationId);
    Flux<Debt> findByUserId(String userId);
    Flux<Debt> findByOrganizationIdAndStatus(String organizationId, String status);
    Flux<Debt> findPendingByOrganizationId(String organizationId);
    Mono<Double> sumPendingAmountByUserId(String userId);
    Mono<Long> countByOrganizationIdAndStatus(String organizationId, String status);
}