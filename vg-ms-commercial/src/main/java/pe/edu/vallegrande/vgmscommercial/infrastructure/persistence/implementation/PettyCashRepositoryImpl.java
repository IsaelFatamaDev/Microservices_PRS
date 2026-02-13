package pe.edu.vallegrande.vgmscommercial.infrastructure.persistence.implementation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import pe.edu.vallegrande.vgmscommercial.domain.models.PettyCash;
import pe.edu.vallegrande.vgmscommercial.domain.models.PettyCashMovement;
import pe.edu.vallegrande.vgmscommercial.domain.models.valueobjects.MovementCategory;
import pe.edu.vallegrande.vgmscommercial.domain.models.valueobjects.MovementType;
import pe.edu.vallegrande.vgmscommercial.domain.models.valueobjects.PettyCashStatus;
import pe.edu.vallegrande.vgmscommercial.domain.models.valueobjects.RecordStatus;
import pe.edu.vallegrande.vgmscommercial.domain.ports.out.IPettyCashRepository;
import pe.edu.vallegrande.vgmscommercial.infrastructure.persistence.entity.PettyCashEntity;
import pe.edu.vallegrande.vgmscommercial.infrastructure.persistence.entity.PettyCashMovementEntity;
import pe.edu.vallegrande.vgmscommercial.infrastructure.persistence.repository.IPettyCashMovementR2dbcRepository;
import pe.edu.vallegrande.vgmscommercial.infrastructure.persistence.repository.IPettyCashR2dbcRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class PettyCashRepositoryImpl implements IPettyCashRepository {

     private final IPettyCashR2dbcRepository pettyCashR2dbc;
     private final IPettyCashMovementR2dbcRepository movementR2dbc;

     @Override
     public Mono<PettyCash> save(PettyCash pettyCash) {
          PettyCashEntity entity = toEntity(pettyCash);
          entity.setNewEntity(pettyCash.getCreatedAt() != null && pettyCash.getUpdatedAt() == null);
          return pettyCashR2dbc.save(entity).map(this::toDomain);
     }

     @Override
     public Mono<PettyCash> findById(String id) {
          return pettyCashR2dbc.findById(id).map(this::toDomain);
     }

     @Override
     public Flux<PettyCash> findByOrganizationId(String organizationId) {
          return pettyCashR2dbc.findByOrganizationId(organizationId).map(this::toDomain);
     }

     @Override
     public Mono<PettyCash> findActiveByOrganizationId(String organizationId) {
          return pettyCashR2dbc.findActiveByOrganizationId(organizationId).map(this::toDomain);
     }

     @Override
     public Mono<PettyCashMovement> saveMovement(PettyCashMovement movement) {
          PettyCashMovementEntity entity = toMovementEntity(movement);
          entity.setNewEntity(true);
          return movementR2dbc.save(entity).map(this::toMovementDomain);
     }

     @Override
     public Flux<PettyCashMovement> findMovementsByPettyCashId(String pettyCashId) {
          return movementR2dbc.findByPettyCashId(pettyCashId).map(this::toMovementDomain);
     }

     @Override
     public Mono<Long> countByOrganizationIdAndStatus(String organizationId, String status) {
          return pettyCashR2dbc.countByOrganizationIdAndPettyCashStatus(organizationId, status);
     }

     private PettyCashEntity toEntity(PettyCash pc) {
          return PettyCashEntity.builder()
                    .id(pc.getId())
                    .organizationId(pc.getOrganizationId())
                    .recordStatus(pc.getRecordStatus().name())
                    .createdAt(pc.getCreatedAt())
                    .createdBy(pc.getCreatedBy())
                    .updatedAt(pc.getUpdatedAt())
                    .updatedBy(pc.getUpdatedBy())
                    .responsibleUserId(pc.getResponsibleUserId())
                    .currentBalance(pc.getCurrentBalance())
                    .maxAmountLimit(pc.getMaxAmountLimit())
                    .pettyCashStatus(pc.getPettyCashStatus().name())
                    .build();
     }

     private PettyCash toDomain(PettyCashEntity e) {
          return PettyCash.builder()
                    .id(e.getId())
                    .organizationId(e.getOrganizationId())
                    .recordStatus(RecordStatus.valueOf(e.getRecordStatus()))
                    .createdAt(e.getCreatedAt())
                    .createdBy(e.getCreatedBy())
                    .updatedAt(e.getUpdatedAt())
                    .updatedBy(e.getUpdatedBy())
                    .responsibleUserId(e.getResponsibleUserId())
                    .currentBalance(e.getCurrentBalance())
                    .maxAmountLimit(e.getMaxAmountLimit())
                    .pettyCashStatus(PettyCashStatus.valueOf(e.getPettyCashStatus()))
                    .build();
     }

     private PettyCashMovementEntity toMovementEntity(PettyCashMovement m) {
          return PettyCashMovementEntity.builder()
                    .id(m.getId())
                    .organizationId(m.getOrganizationId())
                    .createdAt(m.getCreatedAt())
                    .createdBy(m.getCreatedBy())
                    .pettyCashId(m.getPettyCashId())
                    .movementDate(m.getMovementDate())
                    .movementType(m.getMovementType().name())
                    .amount(m.getAmount())
                    .category(m.getCategory().name())
                    .description(m.getDescription())
                    .voucherNumber(m.getVoucherNumber())
                    .previousBalance(m.getPreviousBalance())
                    .newBalance(m.getNewBalance())
                    .build();
     }

     private PettyCashMovement toMovementDomain(PettyCashMovementEntity e) {
          return PettyCashMovement.builder()
                    .id(e.getId())
                    .organizationId(e.getOrganizationId())
                    .createdAt(e.getCreatedAt())
                    .createdBy(e.getCreatedBy())
                    .pettyCashId(e.getPettyCashId())
                    .movementDate(e.getMovementDate())
                    .movementType(MovementType.valueOf(e.getMovementType()))
                    .amount(e.getAmount())
                    .category(MovementCategory.valueOf(e.getCategory()))
                    .description(e.getDescription())
                    .voucherNumber(e.getVoucherNumber())
                    .previousBalance(e.getPreviousBalance())
                    .newBalance(e.getNewBalance())
                    .build();
     }
}
