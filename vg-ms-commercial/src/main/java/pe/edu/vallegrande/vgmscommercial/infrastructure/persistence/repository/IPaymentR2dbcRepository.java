package pe.edu.vallegrande.vgmscommercial.infrastructure.persistence.repository;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import pe.edu.vallegrande.vgmscommercial.infrastructure.persistence.entity.PaymentEntity;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface IPaymentR2dbcRepository extends R2dbcRepository<PaymentEntity, String> {

     Flux<PaymentEntity> findByOrganizationId(String organizationId);

     Flux<PaymentEntity> findByUserId(String userId);

     Flux<PaymentEntity> findByOrganizationIdAndPaymentStatus(String organizationId, String paymentStatus);

     Mono<Long> countByOrganizationIdAndPaymentStatus(String organizationId, String paymentStatus);

     @Query("SELECT COUNT(*) FROM payments WHERE organization_id = :orgId")
     Mono<Long> countByOrganizationId(String orgId);
}
