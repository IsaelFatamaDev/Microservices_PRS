package pe.edu.vallegrande.vgmscommercial.infrastructure.persistence.repository;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import pe.edu.vallegrande.vgmscommercial.infrastructure.persistence.entity.DebtEntity;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface IDebtR2dbcRepository extends R2dbcRepository<DebtEntity, String> {

     Flux<DebtEntity> findByOrganizationId(String organizationId);

     Flux<DebtEntity> findByUserId(String userId);

     Flux<DebtEntity> findByOrganizationIdAndDebtStatus(String organizationId, String debtStatus);

     @Query("SELECT * FROM debts WHERE organization_id = :orgId AND debt_status IN ('PENDING', 'PARTIAL') AND record_status = 'ACTIVE'")
     Flux<DebtEntity> findPendingByOrganizationId(String orgId);

     @Query("SELECT COALESCE(SUM(pending_amount + COALESCE(late_fee, 0)), 0) FROM debts WHERE user_id = :userId AND debt_status IN ('PENDING', 'PARTIAL') AND record_status = 'ACTIVE'")
     Mono<Double> sumPendingAmountByUserId(String userId);

     Mono<Long> countByOrganizationIdAndDebtStatus(String organizationId, String debtStatus);
}
