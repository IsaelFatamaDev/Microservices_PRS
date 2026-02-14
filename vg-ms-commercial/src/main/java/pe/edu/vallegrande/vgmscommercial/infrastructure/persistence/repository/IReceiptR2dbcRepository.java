package pe.edu.vallegrande.vgmscommercial.infrastructure.persistence.repository;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import pe.edu.vallegrande.vgmscommercial.infrastructure.persistence.entity.ReceiptEntity;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface IReceiptR2dbcRepository extends R2dbcRepository<ReceiptEntity, String> {

     Flux<ReceiptEntity> findByOrganizationIdAndRecordStatus(String organizationId, String recordStatus);

     Flux<ReceiptEntity> findByUserIdAndRecordStatus(String userId, String recordStatus);

     Flux<ReceiptEntity> findByOrganizationIdAndReceiptStatusAndRecordStatus(String organizationId, String receiptStatus, String recordStatus);

     Flux<ReceiptEntity> findByOrganizationIdAndPeriodMonthAndPeriodYearAndRecordStatus(String organizationId, Integer periodMonth,
               Integer periodYear, String recordStatus);

     Mono<Boolean> existsByUserIdAndPeriodMonthAndPeriodYearAndRecordStatus(String userId, Integer periodMonth, Integer periodYear, String recordStatus);

     Mono<Long> countByOrganizationIdAndReceiptStatusAndRecordStatus(String organizationId, String receiptStatus, String recordStatus);

     @Query("SELECT COUNT(*) FROM receipts WHERE organization_id = :orgId AND period_year = :year")
     Mono<Long> countByOrganizationIdAndYear(String orgId, Integer year);
}
