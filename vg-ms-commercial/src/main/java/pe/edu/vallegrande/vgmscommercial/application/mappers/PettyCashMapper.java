package pe.edu.vallegrande.vgmscommercial.application.mappers;

import org.springframework.stereotype.Component;
import pe.edu.vallegrande.vgmscommercial.application.dto.request.pettycash.CreatePettyCashRequest;
import pe.edu.vallegrande.vgmscommercial.application.dto.request.pettycash.RegisterMovementRequest;
import pe.edu.vallegrande.vgmscommercial.application.dto.response.PettyCashMovementResponse;
import pe.edu.vallegrande.vgmscommercial.application.dto.response.PettyCashResponse;
import pe.edu.vallegrande.vgmscommercial.domain.models.PettyCash;
import pe.edu.vallegrande.vgmscommercial.domain.models.PettyCashMovement;
import pe.edu.vallegrande.vgmscommercial.domain.models.valueobjects.MovementCategory;
import pe.edu.vallegrande.vgmscommercial.domain.models.valueobjects.MovementType;
import pe.edu.vallegrande.vgmscommercial.domain.models.valueobjects.PettyCashStatus;
import pe.edu.vallegrande.vgmscommercial.domain.models.valueobjects.RecordStatus;

import java.time.LocalDateTime;
import java.util.UUID;

@Component
public class PettyCashMapper {

     public PettyCash toDomain(CreatePettyCashRequest request, String organizationId, String createdBy) {
          return PettyCash.builder()
                    .id(UUID.randomUUID().toString())
                    .organizationId(organizationId)
                    .recordStatus(RecordStatus.ACTIVE)
                    .createdAt(LocalDateTime.now())
                    .createdBy(createdBy)
                    .responsibleUserId(request.getResponsibleUserId())
                    .currentBalance(request.getInitialBalance())
                    .maxAmountLimit(request.getMaxAmountLimit())
                    .pettyCashStatus(PettyCashStatus.ACTIVE)
                    .build();
     }

     public PettyCashResponse toResponse(PettyCash pettyCash) {
          return PettyCashResponse.builder()
                    .id(pettyCash.getId())
                    .organizationId(pettyCash.getOrganizationId())
                    .recordStatus(pettyCash.getRecordStatus().name())
                    .createdAt(pettyCash.getCreatedAt())
                    .createdBy(pettyCash.getCreatedBy())
                    .updatedAt(pettyCash.getUpdatedAt())
                    .updatedBy(pettyCash.getUpdatedBy())
                    .responsibleUserId(pettyCash.getResponsibleUserId())
                    .currentBalance(pettyCash.getCurrentBalance())
                    .maxAmountLimit(pettyCash.getMaxAmountLimit())
                    .pettyCashStatus(pettyCash.getPettyCashStatus().name())
                    .build();
     }

     public PettyCashMovement toMovementDomain(RegisterMovementRequest request, String organizationId, String createdBy,
               Double previousBalance, Double newBalance) {
          return PettyCashMovement.builder()
                    .id(UUID.randomUUID().toString())
                    .organizationId(organizationId)
                    .createdAt(LocalDateTime.now())
                    .createdBy(createdBy)
                    .pettyCashId(request.getPettyCashId())
                    .movementDate(LocalDateTime.now())
                    .movementType(MovementType.valueOf(request.getMovementType()))
                    .amount(request.getAmount())
                    .category(MovementCategory.valueOf(request.getCategory()))
                    .description(request.getDescription())
                    .voucherNumber(request.getVoucherNumber())
                    .previousBalance(previousBalance)
                    .newBalance(newBalance)
                    .build();
     }

     public PettyCashMovementResponse toMovementResponse(PettyCashMovement movement) {
          return PettyCashMovementResponse.builder()
                    .id(movement.getId())
                    .organizationId(movement.getOrganizationId())
                    .createdAt(movement.getCreatedAt())
                    .createdBy(movement.getCreatedBy())
                    .pettyCashId(movement.getPettyCashId())
                    .movementDate(movement.getMovementDate())
                    .movementType(movement.getMovementType().name())
                    .amount(movement.getAmount())
                    .category(movement.getCategory().name())
                    .description(movement.getDescription())
                    .voucherNumber(movement.getVoucherNumber())
                    .previousBalance(movement.getPreviousBalance())
                    .newBalance(movement.getNewBalance())
                    .build();
     }
}
