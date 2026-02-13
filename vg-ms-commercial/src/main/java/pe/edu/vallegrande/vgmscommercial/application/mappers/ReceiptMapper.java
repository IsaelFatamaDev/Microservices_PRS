package pe.edu.vallegrande.vgmscommercial.application.mappers;

import org.springframework.stereotype.Component;
import pe.edu.vallegrande.vgmscommercial.application.dto.request.receipt.CreateReceiptDetailRequest;
import pe.edu.vallegrande.vgmscommercial.application.dto.request.receipt.CreateReceiptRequest;
import pe.edu.vallegrande.vgmscommercial.application.dto.response.ReceiptDetailResponse;
import pe.edu.vallegrande.vgmscommercial.application.dto.response.ReceiptResponse;
import pe.edu.vallegrande.vgmscommercial.domain.models.Receipt;
import pe.edu.vallegrande.vgmscommercial.domain.models.ReceiptDetail;
import pe.edu.vallegrande.vgmscommercial.domain.models.valueobjects.ConceptType;
import pe.edu.vallegrande.vgmscommercial.domain.models.valueobjects.RecordStatus;
import pe.edu.vallegrande.vgmscommercial.domain.models.valueobjects.ReceiptStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class ReceiptMapper {

     public Receipt toDomain(CreateReceiptRequest request, String organizationId, String createdBy,
               String receiptNumber, LocalDateTime dueDate) {
          List<ReceiptDetail> details = request.getDetails().stream()
                    .map(this::toDetailDomain)
                    .collect(Collectors.toList());

          return Receipt.builder()
                    .id(UUID.randomUUID().toString())
                    .organizationId(organizationId)
                    .recordStatus(RecordStatus.ACTIVE)
                    .createdAt(LocalDateTime.now())
                    .createdBy(createdBy)
                    .receiptNumber(receiptNumber)
                    .userId(request.getUserId())
                    .periodMonth(request.getPeriodMonth())
                    .periodYear(request.getPeriodYear())
                    .issueDate(LocalDateTime.now())
                    .dueDate(dueDate)
                    .totalAmount(request.getTotalAmount())
                    .paidAmount(0.0)
                    .pendingAmount(request.getTotalAmount())
                    .receiptStatus(ReceiptStatus.PENDING)
                    .notes(request.getNotes())
                    .details(details)
                    .build();
     }

     private ReceiptDetail toDetailDomain(CreateReceiptDetailRequest request) {
          return ReceiptDetail.builder()
                    .id(UUID.randomUUID().toString())
                    .conceptType(ConceptType.valueOf(request.getConceptType()))
                    .description(request.getDescription())
                    .amount(request.getAmount())
                    .createdAt(LocalDateTime.now())
                    .build();
     }

     public ReceiptResponse toResponse(Receipt receipt) {
          return ReceiptResponse.builder()
                    .id(receipt.getId())
                    .organizationId(receipt.getOrganizationId())
                    .recordStatus(receipt.getRecordStatus().name())
                    .createdAt(receipt.getCreatedAt())
                    .createdBy(receipt.getCreatedBy())
                    .updatedAt(receipt.getUpdatedAt())
                    .updatedBy(receipt.getUpdatedBy())
                    .receiptNumber(receipt.getReceiptNumber())
                    .userId(receipt.getUserId())
                    .periodMonth(receipt.getPeriodMonth())
                    .periodYear(receipt.getPeriodYear())
                    .periodDescription(receipt.getPeriodDescription())
                    .issueDate(receipt.getIssueDate())
                    .dueDate(receipt.getDueDate())
                    .totalAmount(receipt.getTotalAmount())
                    .paidAmount(receipt.getPaidAmount())
                    .pendingAmount(receipt.getPendingAmount())
                    .receiptStatus(receipt.getReceiptStatus().name())
                    .notes(receipt.getNotes())
                    .details(receipt.getDetails() != null
                              ? receipt.getDetails().stream().map(this::toDetailResponse).collect(Collectors.toList())
                              : null)
                    .build();
     }

     private ReceiptDetailResponse toDetailResponse(ReceiptDetail detail) {
          return ReceiptDetailResponse.builder()
                    .id(detail.getId())
                    .receiptId(detail.getReceiptId())
                    .conceptType(detail.getConceptType().name())
                    .description(detail.getDescription())
                    .amount(detail.getAmount())
                    .createdAt(detail.getCreatedAt())
                    .build();
     }
}
