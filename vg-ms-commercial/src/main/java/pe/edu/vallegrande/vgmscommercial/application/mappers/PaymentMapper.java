package pe.edu.vallegrande.vgmscommercial.application.mappers;

import org.springframework.stereotype.Component;
import pe.edu.vallegrande.vgmscommercial.application.dto.request.payment.CreatePaymentDetailRequest;
import pe.edu.vallegrande.vgmscommercial.application.dto.request.payment.CreatePaymentRequest;
import pe.edu.vallegrande.vgmscommercial.application.dto.response.PaymentDetailResponse;
import pe.edu.vallegrande.vgmscommercial.application.dto.response.PaymentResponse;
import pe.edu.vallegrande.vgmscommercial.domain.models.Payment;
import pe.edu.vallegrande.vgmscommercial.domain.models.PaymentDetail;
import pe.edu.vallegrande.vgmscommercial.domain.models.valueobjects.ConceptType;
import pe.edu.vallegrande.vgmscommercial.domain.models.valueobjects.PaymentMethod;
import pe.edu.vallegrande.vgmscommercial.domain.models.valueobjects.PaymentStatus;
import pe.edu.vallegrande.vgmscommercial.domain.models.valueobjects.RecordStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class PaymentMapper {

     public Payment toDomain(CreatePaymentRequest request, String organizationId, String createdBy,
               String receiptNumber) {
          List<PaymentDetail> details = request.getDetails().stream()
                    .map(this::toDetailDomain)
                    .collect(Collectors.toList());

          return Payment.builder()
                    .id(UUID.randomUUID().toString())
                    .organizationId(organizationId)
                    .recordStatus(RecordStatus.ACTIVE)
                    .createdAt(LocalDateTime.now())
                    .createdBy(createdBy)
                    .userId(request.getUserId())
                    .paymentDate(LocalDateTime.now())
                    .totalAmount(request.getTotalAmount())
                    .paymentMethod(PaymentMethod.valueOf(request.getPaymentMethod()))
                    .paymentStatus(PaymentStatus.COMPLETED)
                    .receiptNumber(receiptNumber)
                    .notes(request.getNotes())
                    .details(details)
                    .build();
     }

     private PaymentDetail toDetailDomain(CreatePaymentDetailRequest request) {
          return PaymentDetail.builder()
                    .id(UUID.randomUUID().toString())
                    .paymentType(ConceptType.valueOf(request.getPaymentType()))
                    .description(request.getDescription())
                    .amount(request.getAmount())
                    .periodMonth(request.getPeriodMonth())
                    .periodYear(request.getPeriodYear())
                    .createdAt(LocalDateTime.now())
                    .build();
     }

     public PaymentResponse toResponse(Payment payment) {
          return PaymentResponse.builder()
                    .id(payment.getId())
                    .organizationId(payment.getOrganizationId())
                    .recordStatus(payment.getRecordStatus().name())
                    .createdAt(payment.getCreatedAt())
                    .createdBy(payment.getCreatedBy())
                    .updatedAt(payment.getUpdatedAt())
                    .updatedBy(payment.getUpdatedBy())
                    .userId(payment.getUserId())
                    .paymentDate(payment.getPaymentDate())
                    .totalAmount(payment.getTotalAmount())
                    .paymentMethod(payment.getPaymentMethod().name())
                    .paymentStatus(payment.getPaymentStatus().name())
                    .receiptNumber(payment.getReceiptNumber())
                    .notes(payment.getNotes())
                    .details(payment.getDetails() != null
                              ? payment.getDetails().stream().map(this::toDetailResponse).collect(Collectors.toList())
                              : null)
                    .build();
     }

     private PaymentDetailResponse toDetailResponse(PaymentDetail detail) {
          return PaymentDetailResponse.builder()
                    .id(detail.getId())
                    .paymentId(detail.getPaymentId())
                    .paymentType(detail.getPaymentType().name())
                    .description(detail.getDescription())
                    .amount(detail.getAmount())
                    .periodMonth(detail.getPeriodMonth())
                    .periodYear(detail.getPeriodYear())
                    .periodDescription(detail.getPeriodDescription())
                    .createdAt(detail.getCreatedAt())
                    .build();
     }
}
