package pe.edu.vallegrande.vgmscommercial.infrastructure.persistence.repository;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import pe.edu.vallegrande.vgmscommercial.infrastructure.persistence.entity.PettyCashEntity;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface IPettyCashR2dbcRepository extends R2dbcRepository<PettyCashEntity, String> {

     Flux<PettyCashEntity> findByOrganizationId(String organizationId);

     @Query("SELECT * FROM petty_cash WHERE organization_id = :orgId AND petty_cash_status = 'ACTIVE' AND record_status = 'ACTIVE' LIMIT 1")
     Mono<PettyCashEntity> findActiveByOrganizationId(String orgId);

     Mono<Long> countByOrganizationIdAndPettyCashStatus(String organizationId, String pettyCashStatus);
}
