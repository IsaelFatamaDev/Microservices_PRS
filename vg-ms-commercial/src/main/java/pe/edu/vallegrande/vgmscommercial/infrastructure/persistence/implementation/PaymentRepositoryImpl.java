package pe.edu.vallegrande.vgmscommercial.infrastructure.persistence.implementation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import pe.edu.vallegrande.vgmscommercial.domain.models.Payment;
import pe.edu.vallegrande.vgmscommercial.domain.models.PaymentDetail;
import pe.edu.vallegrande.vgmscommercial.domain.models.valueobjects.ConceptType;
import pe.edu.vallegrande.vgmscommercial.domain.models.valueobjects.PaymentMethod;
import pe.edu.vallegrande.vgmscommercial.domain.models.valueobjects.PaymentStatus;
import pe.edu.vallegrande.vgmscommercial.domain.models.valueobjects.RecordStatus;
import pe.edu.vallegrande.vgmscommercial.domain.ports.out.IPaymentRepository;
import pe.edu.vallegrande.vgmscommercial.infrastructure.persistence.entity.PaymentDetailEntity;
import pe.edu.vallegrande.vgmscommercial.infrastructure.persistence.entity.PaymentEntity;
import pe.edu.vallegrande.vgmscommercial.infrastructure.persistence.repository.IPaymentDetailR2dbcRepository;
import pe.edu.vallegrande.vgmscommercial.infrastructure.persistence.repository.IPaymentR2dbcRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentRepositoryImpl implements IPaymentRepository {

     private final IPaymentR2dbcRepository paymentR2dbc;
     private final IPaymentDetailR2dbcRepository detailR2dbc;

     @Override
     public Mono<Payment> save(Payment payment) {
          PaymentEntity entity = toEntity(payment);
          entity.setNewEntity(payment.getCreatedAt() != null && payment.getUpdatedAt() == null);
          return paymentR2dbc.save(entity)
                    .flatMap(saved -> {
                         if (payment.getDetails() != null && !payment.getDetails().isEmpty()) {
                              List<PaymentDetailEntity> detailEntities = payment.getDetails().stream()
                                        .map(d -> {
                                             PaymentDetailEntity de = toDetailEntity(d, saved.getId());
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
     public Mono<Payment> findById(String id) {
          return paymentR2dbc.findById(id)
                    .flatMap(entity -> detailR2dbc.findByPaymentId(id)
                              .map(this::toDetailDomain)
                              .collectList()
                              .map(details -> toDomain(entity, details)));
     }

     @Override
     public Flux<Payment> findByOrganizationId(String organizationId) {
          return paymentR2dbc.findByOrganizationId(organizationId)
                    .flatMap(entity -> detailR2dbc.findByPaymentId(entity.getId())
                              .map(this::toDetailDomain)
                              .collectList()
                              .map(details -> toDomain(entity, details)));
     }

     @Override
     public Flux<Payment> findByUserId(String userId) {
          return paymentR2dbc.findByUserId(userId)
                    .flatMap(entity -> detailR2dbc.findByPaymentId(entity.getId())
                              .map(this::toDetailDomain)
                              .collectList()
                              .map(details -> toDomain(entity, details)));
     }

     @Override
     public Flux<Payment> findByOrganizationIdAndStatus(String organizationId, String status) {
          return paymentR2dbc.findByOrganizationIdAndPaymentStatus(organizationId, status)
                    .flatMap(entity -> detailR2dbc.findByPaymentId(entity.getId())
                              .map(this::toDetailDomain)
                              .collectList()
                              .map(details -> toDomain(entity, details)));
     }

     @Override
     public Mono<String> generateReceiptNumber(String organizationId) {
          return paymentR2dbc.countByOrganizationId(organizationId)
                    .map(count -> String.format("PAY-%06d", count + 1));
     }

     @Override
     public Mono<Long> countByOrganizationIdAndStatus(String organizationId, String status) {
          return paymentR2dbc.countByOrganizationIdAndPaymentStatus(organizationId, status);
     }

     private PaymentEntity toEntity(Payment p) {
          return PaymentEntity.builder()
                    .id(p.getId())
                    .organizationId(p.getOrganizationId())
                    .recordStatus(p.getRecordStatus().name())
                    .createdAt(p.getCreatedAt())
                    .createdBy(p.getCreatedBy())
                    .updatedAt(p.getUpdatedAt())
                    .updatedBy(p.getUpdatedBy())
                    .userId(p.getUserId())
                    .paymentDate(p.getPaymentDate())
                    .totalAmount(p.getTotalAmount())
                    .paymentMethod(p.getPaymentMethod().name())
                    .paymentStatus(p.getPaymentStatus().name())
                    .receiptNumber(p.getReceiptNumber())
                    .notes(p.getNotes())
                    .build();
     }

     private Payment toDomain(PaymentEntity e, List<PaymentDetail> details) {
          return Payment.builder()
                    .id(e.getId())
                    .organizationId(e.getOrganizationId())
                    .recordStatus(RecordStatus.valueOf(e.getRecordStatus()))
                    .createdAt(e.getCreatedAt())
                    .createdBy(e.getCreatedBy())
                    .updatedAt(e.getUpdatedAt())
                    .updatedBy(e.getUpdatedBy())
                    .userId(e.getUserId())
                    .paymentDate(e.getPaymentDate())
                    .totalAmount(e.getTotalAmount())
                    .paymentMethod(PaymentMethod.valueOf(e.getPaymentMethod()))
                    .paymentStatus(PaymentStatus.valueOf(e.getPaymentStatus()))
                    .receiptNumber(e.getReceiptNumber())
                    .notes(e.getNotes())
                    .details(details)
                    .build();
     }

     private PaymentDetailEntity toDetailEntity(PaymentDetail d, String paymentId) {
          return PaymentDetailEntity.builder()
                    .id(d.getId())
                    .paymentId(paymentId)
                    .paymentType(d.getPaymentType().name())
                    .description(d.getDescription())
                    .amount(d.getAmount())
                    .periodMonth(d.getPeriodMonth())
                    .periodYear(d.getPeriodYear())
                    .createdAt(d.getCreatedAt())
                    .build();
     }

     private PaymentDetail toDetailDomain(PaymentDetailEntity e) {
          return PaymentDetail.builder()
                    .id(e.getId())
                    .paymentId(e.getPaymentId())
                    .paymentType(ConceptType.valueOf(e.getPaymentType()))
                    .description(e.getDescription())
                    .amount(e.getAmount())
                    .periodMonth(e.getPeriodMonth())
                    .periodYear(e.getPeriodYear())
                    .createdAt(e.getCreatedAt())
                    .build();
     }
}
