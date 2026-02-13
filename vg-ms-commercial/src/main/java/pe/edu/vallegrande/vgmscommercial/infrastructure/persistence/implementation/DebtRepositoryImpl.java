package pe.edu.vallegrande.vgmscommercial.infrastructure.persistence.implementation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import pe.edu.vallegrande.vgmscommercial.domain.models.Debt;
import pe.edu.vallegrande.vgmscommercial.domain.models.valueobjects.DebtStatus;
import pe.edu.vallegrande.vgmscommercial.domain.models.valueobjects.RecordStatus;
import pe.edu.vallegrande.vgmscommercial.domain.ports.out.IDebtRepository;
import pe.edu.vallegrande.vgmscommercial.infrastructure.persistence.entity.DebtEntity;
import pe.edu.vallegrande.vgmscommercial.infrastructure.persistence.repository.IDebtR2dbcRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class DebtRepositoryImpl implements IDebtRepository {

     private final IDebtR2dbcRepository debtR2dbc;

     @Override
     public Mono<Debt> save(Debt debt) {
          DebtEntity entity = toEntity(debt);
          entity.setNewEntity(debt.getCreatedAt() != null && debt.getUpdatedAt() == null);
          return debtR2dbc.save(entity).map(this::toDomain);
     }

     @Override
     public Mono<Debt> findById(String id) {
          return debtR2dbc.findById(id).map(this::toDomain);
     }

     @Override
     public Flux<Debt> findByOrganizationId(String organizationId) {
          return debtR2dbc.findByOrganizationId(organizationId).map(this::toDomain);
     }

     @Override
     public Flux<Debt> findByUserId(String userId) {
          return debtR2dbc.findByUserId(userId).map(this::toDomain);
     }

     @Override
     public Flux<Debt> findByOrganizationIdAndStatus(String organizationId, String status) {
          return debtR2dbc.findByOrganizationIdAndDebtStatus(organizationId, status).map(this::toDomain);
     }

     @Override
     public Flux<Debt> findPendingByOrganizationId(String organizationId) {
          return debtR2dbc.findPendingByOrganizationId(organizationId).map(this::toDomain);
     }

     @Override
     public Mono<Double> sumPendingAmountByUserId(String userId) {
          return debtR2dbc.sumPendingAmountByUserId(userId);
     }

     @Override
     public Mono<Long> countByOrganizationIdAndStatus(String organizationId, String status) {
          return debtR2dbc.countByOrganizationIdAndDebtStatus(organizationId, status);
     }

     private DebtEntity toEntity(Debt d) {
          return DebtEntity.builder()
                    .id(d.getId())
                    .organizationId(d.getOrganizationId())
                    .recordStatus(d.getRecordStatus().name())
                    .createdAt(d.getCreatedAt())
                    .createdBy(d.getCreatedBy())
                    .updatedAt(d.getUpdatedAt())
                    .updatedBy(d.getUpdatedBy())
                    .userId(d.getUserId())
                    .periodMonth(d.getPeriodMonth())
                    .periodYear(d.getPeriodYear())
                    .originalAmount(d.getOriginalAmount())
                    .pendingAmount(d.getPendingAmount())
                    .lateFee(d.getLateFee())
                    .debtStatus(d.getDebtStatus().name())
                    .dueDate(d.getDueDate())
                    .build();
     }

     private Debt toDomain(DebtEntity e) {
          return Debt.builder()
                    .id(e.getId())
                    .organizationId(e.getOrganizationId())
                    .recordStatus(RecordStatus.valueOf(e.getRecordStatus()))
                    .createdAt(e.getCreatedAt())
                    .createdBy(e.getCreatedBy())
                    .updatedAt(e.getUpdatedAt())
                    .updatedBy(e.getUpdatedBy())
                    .userId(e.getUserId())
                    .periodMonth(e.getPeriodMonth())
                    .periodYear(e.getPeriodYear())
                    .originalAmount(e.getOriginalAmount())
                    .pendingAmount(e.getPendingAmount())
                    .lateFee(e.getLateFee())
                    .debtStatus(DebtStatus.valueOf(e.getDebtStatus()))
                    .dueDate(e.getDueDate())
                    .build();
     }
}
