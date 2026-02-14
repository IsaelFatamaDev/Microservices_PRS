package pe.edu.vallegrande.vgmscommercial.infrastructure.persistence.implementation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import pe.edu.vallegrande.vgmscommercial.domain.models.Receipt;
import pe.edu.vallegrande.vgmscommercial.domain.models.ReceiptDetail;
import pe.edu.vallegrande.vgmscommercial.domain.models.valueobjects.ConceptType;
import pe.edu.vallegrande.vgmscommercial.domain.models.valueobjects.RecordStatus;
import pe.edu.vallegrande.vgmscommercial.domain.models.valueobjects.ReceiptStatus;
import pe.edu.vallegrande.vgmscommercial.domain.ports.out.IReceiptRepository;
import pe.edu.vallegrande.vgmscommercial.infrastructure.persistence.entity.ReceiptDetailEntity;
import pe.edu.vallegrande.vgmscommercial.infrastructure.persistence.entity.ReceiptEntity;
import pe.edu.vallegrande.vgmscommercial.infrastructure.persistence.repository.IReceiptDetailR2dbcRepository;
import pe.edu.vallegrande.vgmscommercial.infrastructure.persistence.repository.IReceiptR2dbcRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReceiptRepositoryImpl implements IReceiptRepository {

     private final IReceiptR2dbcRepository receiptR2dbc;
     private final IReceiptDetailR2dbcRepository detailR2dbc;

     @Override
     public Mono<Receipt> save(Receipt receipt) {
          ReceiptEntity entity = toEntity(receipt);
          entity.setNewEntity(receipt.getCreatedAt() != null && receipt.getUpdatedAt() == null);
          return receiptR2dbc.save(entity)
                    .flatMap(saved -> {
                         if (receipt.getDetails() != null && !receipt.getDetails().isEmpty()) {
                              List<ReceiptDetailEntity> detailEntities = receipt.getDetails().stream()
                                        .map(d -> {
                                             ReceiptDetailEntity de = toDetailEntity(d, saved.getId());
                                             de.setNewEntity(true);
                                             return de;
                                        })
                                        .collect(Collectors.toList());
                              return Flux.fromIterable(detailEntities)
                                        .flatMap(detailR2dbc::save)
                                        .collectList()
                                        .map(details -> toDomain(saved, details.stream().map(this::toDetailDomain)
                                                  .collect(Collectors.toList())));
                         }
                         return Mono.just(toDomain(saved, Collections.emptyList()));
                    });
     }

     @Override
     public Mono<Receipt> findById(String id) {
          return receiptR2dbc.findById(id)
                    .flatMap(entity -> detailR2dbc.findByReceiptId(id)
                              .map(this::toDetailDomain)
                              .collectList()
                              .map(details -> toDomain(entity, details)));
     }

     @Override
     public Flux<Receipt> findByOrganizationId(String organizationId) {
          log.info("Searching receipts for organizationId: {}", organizationId);
          return receiptR2dbc.findByOrganizationIdAndRecordStatus(organizationId, "ACTIVE")
                    .flatMap(entity -> {
                         log.debug("Found receipt: {} for org: {}", entity.getId(), entity.getOrganizationId());
                         return detailR2dbc.findByReceiptId(entity.getId())
                                   .map(this::toDetailDomain)
                                   .collectList()
                                   .map(details -> toDomain(entity, details));
                    })
                    .doOnComplete(() -> log.info("Completed receipt search for organizationId: {}", organizationId));
     }

     @Override
     public Flux<Receipt> findByUserId(String userId) {
          log.info("Searching receipts for userId: {}", userId);
          return receiptR2dbc.findByUserIdAndRecordStatus(userId, "ACTIVE")
                    .flatMap(entity -> detailR2dbc.findByReceiptId(entity.getId())
                              .map(this::toDetailDomain)
                              .collectList()
                              .map(details -> toDomain(entity, details)));
     }

     @Override
     public Flux<Receipt> findByOrganizationIdAndStatus(String organizationId, String status) {
          return receiptR2dbc.findByOrganizationIdAndReceiptStatusAndRecordStatus(organizationId, status, "ACTIVE")
                    .flatMap(entity -> detailR2dbc.findByReceiptId(entity.getId())
                              .map(this::toDetailDomain)
                              .collectList()
                              .map(details -> toDomain(entity, details)));
     }

     @Override
     public Flux<Receipt> findByOrganizationIdAndPeriod(String organizationId, Integer month, Integer year) {
          return receiptR2dbc.findByOrganizationIdAndPeriodMonthAndPeriodYearAndRecordStatus(organizationId, month, year, "ACTIVE")
                    .flatMap(entity -> detailR2dbc.findByReceiptId(entity.getId())
                              .map(this::toDetailDomain)
                              .collectList()
                              .map(details -> toDomain(entity, details)));
     }

     @Override
     public Mono<Boolean> existsByUserIdAndPeriod(String userId, Integer month, Integer year) {
          return receiptR2dbc.existsByUserIdAndPeriodMonthAndPeriodYearAndRecordStatus(userId, month, year, "ACTIVE");
     }

     @Override
     public Mono<String> generateReceiptNumber(String organizationId, Integer year) {
          return receiptR2dbc.countByOrganizationIdAndYear(organizationId, year)
                    .map(count -> String.format("REC-%d-%06d", year, count + 1));
     }

     @Override
     public Mono<Long> countByOrganizationIdAndStatus(String organizationId, String status) {
          return receiptR2dbc.countByOrganizationIdAndReceiptStatusAndRecordStatus(organizationId, status, "ACTIVE");
     }

     private ReceiptEntity toEntity(Receipt r) {
          return ReceiptEntity.builder()
                    .id(r.getId())
                    .organizationId(r.getOrganizationId())
                    .recordStatus(r.getRecordStatus().name())
                    .createdAt(r.getCreatedAt())
                    .createdBy(r.getCreatedBy())
                    .updatedAt(r.getUpdatedAt())
                    .updatedBy(r.getUpdatedBy())
                    .receiptNumber(r.getReceiptNumber())
                    .userId(r.getUserId())
                    .periodMonth(r.getPeriodMonth())
                    .periodYear(r.getPeriodYear())
                    .issueDate(r.getIssueDate())
                    .dueDate(r.getDueDate())
                    .totalAmount(r.getTotalAmount())
                    .paidAmount(r.getPaidAmount())
                    .pendingAmount(r.getPendingAmount())
                    .receiptStatus(r.getReceiptStatus().name())
                    .notes(r.getNotes())
                    .build();
     }

     private Receipt toDomain(ReceiptEntity e, List<ReceiptDetail> details) {
          return Receipt.builder()
                    .id(e.getId())
                    .organizationId(e.getOrganizationId())
                    .recordStatus(RecordStatus.valueOf(e.getRecordStatus()))
                    .createdAt(e.getCreatedAt())
                    .createdBy(e.getCreatedBy())
                    .updatedAt(e.getUpdatedAt())
                    .updatedBy(e.getUpdatedBy())
                    .receiptNumber(e.getReceiptNumber())
                    .userId(e.getUserId())
                    .periodMonth(e.getPeriodMonth())
                    .periodYear(e.getPeriodYear())
                    .issueDate(e.getIssueDate())
                    .dueDate(e.getDueDate())
                    .totalAmount(e.getTotalAmount())
                    .paidAmount(e.getPaidAmount())
                    .pendingAmount(e.getPendingAmount())
                    .receiptStatus(ReceiptStatus.valueOf(e.getReceiptStatus()))
                    .notes(e.getNotes())
                    .details(details)
                    .build();
     }

     private ReceiptDetailEntity toDetailEntity(ReceiptDetail d, String receiptId) {
          return ReceiptDetailEntity.builder()
                    .id(d.getId())
                    .receiptId(receiptId)
                    .conceptType(d.getConceptType().name())
                    .description(d.getDescription())
                    .amount(d.getAmount())
                    .createdAt(d.getCreatedAt())
                    .build();
     }

     private ReceiptDetail toDetailDomain(ReceiptDetailEntity e) {
          return ReceiptDetail.builder()
                    .id(e.getId())
                    .receiptId(e.getReceiptId())
                    .conceptType(ConceptType.valueOf(e.getConceptType()))
                    .description(e.getDescription())
                    .amount(e.getAmount())
                    .createdAt(e.getCreatedAt())
                    .build();
     }
}
