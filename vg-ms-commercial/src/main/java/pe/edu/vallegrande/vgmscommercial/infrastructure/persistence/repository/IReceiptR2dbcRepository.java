package pe.edu.vallegrande.vgmscommercial.infrastructure.persistence.repository;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import pe.edu.vallegrande.vgmscommercial.infrastructure.persistence.entity.ReceiptEntity;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface IReceiptR2dbcRepository extends R2dbcRepository<ReceiptEntity, String> {

     Flux<ReceiptEntity> findByOrganizationId(String organizationId);

     Flux<ReceiptEntity> findByUserId(String userId);

     Flux<ReceiptEntity> findByOrganizationIdAndReceiptStatus(String organizationId, String receiptStatus);

     Flux<ReceiptEntity> findByOrganizationIdAndPeriodMonthAndPeriodYear(String organizationId, Integer periodMonth,
               Integer periodYear);

     Mono<Boolean> existsByUserIdAndPeriodMonthAndPeriodYear(String userId, Integer periodMonth, Integer periodYear);

     Mono<Long> countByOrganizationIdAndReceiptStatus(String organizationId, String receiptStatus);

     @Query("SELECT COUNT(*) FROM receipts WHERE organization_id = :orgId AND period_year = :year")
     Mono<Long> countByOrganizationIdAndYear(String orgId, Integer year);
}
