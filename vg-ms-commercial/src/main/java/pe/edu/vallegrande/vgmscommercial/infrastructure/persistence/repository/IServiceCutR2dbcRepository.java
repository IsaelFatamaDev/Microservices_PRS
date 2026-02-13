package pe.edu.vallegrande.vgmscommercial.infrastructure.persistence.repository;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import pe.edu.vallegrande.vgmscommercial.infrastructure.persistence.entity.ServiceCutEntity;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface IServiceCutR2dbcRepository extends R2dbcRepository<ServiceCutEntity, String> {

     Flux<ServiceCutEntity> findByOrganizationId(String organizationId);

     Flux<ServiceCutEntity> findByUserId(String userId);

     Flux<ServiceCutEntity> findByOrganizationIdAndCutStatus(String organizationId, String cutStatus);

     @Query("SELECT * FROM service_cuts WHERE organization_id = :orgId AND cut_status = 'PENDING' AND record_status = 'ACTIVE'")
     Flux<ServiceCutEntity> findPendingByOrganizationId(String orgId);

     @Query("SELECT EXISTS(SELECT 1 FROM service_cuts WHERE user_id = :userId AND cut_status IN ('PENDING', 'EXECUTED') AND record_status = 'ACTIVE')")
     Mono<Boolean> existsActiveByUserId(String userId);

     Mono<Long> countByOrganizationIdAndCutStatus(String organizationId, String cutStatus);
}
