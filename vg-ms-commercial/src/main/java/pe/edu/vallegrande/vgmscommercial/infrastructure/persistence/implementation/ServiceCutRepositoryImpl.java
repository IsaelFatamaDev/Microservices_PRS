package pe.edu.vallegrande.vgmscommercial.infrastructure.persistence.implementation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import pe.edu.vallegrande.vgmscommercial.domain.models.ServiceCut;
import pe.edu.vallegrande.vgmscommercial.domain.models.valueobjects.CutReason;
import pe.edu.vallegrande.vgmscommercial.domain.models.valueobjects.CutStatus;
import pe.edu.vallegrande.vgmscommercial.domain.models.valueobjects.RecordStatus;
import pe.edu.vallegrande.vgmscommercial.domain.ports.out.IServiceCutRepository;
import pe.edu.vallegrande.vgmscommercial.infrastructure.persistence.entity.ServiceCutEntity;
import pe.edu.vallegrande.vgmscommercial.infrastructure.persistence.repository.IServiceCutR2dbcRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class ServiceCutRepositoryImpl implements IServiceCutRepository {

     private final IServiceCutR2dbcRepository serviceCutR2dbc;

     @Override
     public Mono<ServiceCut> save(ServiceCut serviceCut) {
          ServiceCutEntity entity = toEntity(serviceCut);
          entity.setNewEntity(serviceCut.getCreatedAt() != null && serviceCut.getUpdatedAt() == null);
          return serviceCutR2dbc.save(entity).map(this::toDomain);
     }

     @Override
     public Mono<ServiceCut> findById(String id) {
          return serviceCutR2dbc.findById(id).map(this::toDomain);
     }

     @Override
     public Flux<ServiceCut> findByOrganizationId(String organizationId) {
          return serviceCutR2dbc.findByOrganizationId(organizationId).map(this::toDomain);
     }

     @Override
     public Flux<ServiceCut> findByUserId(String userId) {
          return serviceCutR2dbc.findByUserId(userId).map(this::toDomain);
     }

     @Override
     public Flux<ServiceCut> findByOrganizationIdAndStatus(String organizationId, String status) {
          return serviceCutR2dbc.findByOrganizationIdAndCutStatus(organizationId, status).map(this::toDomain);
     }

     @Override
     public Flux<ServiceCut> findPendingByOrganizationId(String organizationId) {
          return serviceCutR2dbc.findPendingByOrganizationId(organizationId).map(this::toDomain);
     }

     @Override
     public Mono<Boolean> existsActiveByUserId(String userId) {
          return serviceCutR2dbc.existsActiveByUserId(userId);
     }

     @Override
     public Mono<Long> countByOrganizationIdAndStatus(String organizationId, String status) {
          return serviceCutR2dbc.countByOrganizationIdAndCutStatus(organizationId, status);
     }

     private ServiceCutEntity toEntity(ServiceCut sc) {
          return ServiceCutEntity.builder()
                    .id(sc.getId())
                    .organizationId(sc.getOrganizationId())
                    .recordStatus(sc.getRecordStatus().name())
                    .createdAt(sc.getCreatedAt())
                    .createdBy(sc.getCreatedBy())
                    .updatedAt(sc.getUpdatedAt())
                    .updatedBy(sc.getUpdatedBy())
                    .userId(sc.getUserId())
                    .waterBoxId(sc.getWaterBoxId())
                    .scheduledDate(sc.getScheduledDate())
                    .executedDate(sc.getExecutedDate())
                    .cutReason(sc.getCutReason().name())
                    .debtAmount(sc.getDebtAmount())
                    .reconnectionDate(sc.getReconnectionDate())
                    .reconnectionFeePaid(sc.getReconnectionFeePaid())
                    .cutStatus(sc.getCutStatus().name())
                    .notes(sc.getNotes())
                    .build();
     }

     private ServiceCut toDomain(ServiceCutEntity e) {
          return ServiceCut.builder()
                    .id(e.getId())
                    .organizationId(e.getOrganizationId())
                    .recordStatus(RecordStatus.valueOf(e.getRecordStatus()))
                    .createdAt(e.getCreatedAt())
                    .createdBy(e.getCreatedBy())
                    .updatedAt(e.getUpdatedAt())
                    .updatedBy(e.getUpdatedBy())
                    .userId(e.getUserId())
                    .waterBoxId(e.getWaterBoxId())
                    .scheduledDate(e.getScheduledDate())
                    .executedDate(e.getExecutedDate())
                    .cutReason(CutReason.valueOf(e.getCutReason()))
                    .debtAmount(e.getDebtAmount())
                    .reconnectionDate(e.getReconnectionDate())
                    .reconnectionFeePaid(e.getReconnectionFeePaid())
                    .cutStatus(CutStatus.valueOf(e.getCutStatus()))
                    .notes(e.getNotes())
                    .build();
     }
}
