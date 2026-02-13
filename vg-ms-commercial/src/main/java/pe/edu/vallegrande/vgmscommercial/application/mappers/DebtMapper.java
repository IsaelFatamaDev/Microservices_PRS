package pe.edu.vallegrande.vgmscommercial.application.mappers;

import org.springframework.stereotype.Component;
import pe.edu.vallegrande.vgmscommercial.application.dto.request.debt.CreateDebtRequest;
import pe.edu.vallegrande.vgmscommercial.application.dto.response.DebtResponse;
import pe.edu.vallegrande.vgmscommercial.domain.models.Debt;
import pe.edu.vallegrande.vgmscommercial.domain.models.valueobjects.DebtStatus;
import pe.edu.vallegrande.vgmscommercial.domain.models.valueobjects.RecordStatus;

import java.time.LocalDateTime;
import java.util.UUID;

@Component
public class DebtMapper {

     public Debt toDomain(CreateDebtRequest request, String organizationId, String createdBy, LocalDateTime dueDate) {
          return Debt.builder()
                    .id(UUID.randomUUID().toString())
                    .organizationId(organizationId)
                    .recordStatus(RecordStatus.ACTIVE)
                    .createdAt(LocalDateTime.now())
                    .createdBy(createdBy)
                    .userId(request.getUserId())
                    .periodMonth(request.getPeriodMonth())
                    .periodYear(request.getPeriodYear())
                    .originalAmount(request.getOriginalAmount())
                    .pendingAmount(request.getOriginalAmount())
                    .lateFee(0.0)
                    .debtStatus(DebtStatus.PENDING)
                    .dueDate(dueDate)
                    .build();
     }

     public DebtResponse toResponse(Debt debt) {
          return DebtResponse.builder()
                    .id(debt.getId())
                    .organizationId(debt.getOrganizationId())
                    .recordStatus(debt.getRecordStatus().name())
                    .createdAt(debt.getCreatedAt())
                    .createdBy(debt.getCreatedBy())
                    .updatedAt(debt.getUpdatedAt())
                    .updatedBy(debt.getUpdatedBy())
                    .userId(debt.getUserId())
                    .periodMonth(debt.getPeriodMonth())
                    .periodYear(debt.getPeriodYear())
                    .periodDescription(debt.getPeriodDescription())
                    .originalAmount(debt.getOriginalAmount())
                    .pendingAmount(debt.getPendingAmount())
                    .lateFee(debt.getLateFee())
                    .totalAmount(debt.getTotalAmount())
                    .debtStatus(debt.getDebtStatus().name())
                    .dueDate(debt.getDueDate())
                    .build();
     }
}
