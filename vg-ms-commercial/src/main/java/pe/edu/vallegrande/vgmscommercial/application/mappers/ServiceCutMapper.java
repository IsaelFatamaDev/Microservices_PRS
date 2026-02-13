package pe.edu.vallegrande.vgmscommercial.application.mappers;

import org.springframework.stereotype.Component;
import pe.edu.vallegrande.vgmscommercial.application.dto.request.servicecut.CreateServiceCutRequest;
import pe.edu.vallegrande.vgmscommercial.application.dto.response.ServiceCutResponse;
import pe.edu.vallegrande.vgmscommercial.domain.models.ServiceCut;
import pe.edu.vallegrande.vgmscommercial.domain.models.valueobjects.CutReason;
import pe.edu.vallegrande.vgmscommercial.domain.models.valueobjects.CutStatus;
import pe.edu.vallegrande.vgmscommercial.domain.models.valueobjects.RecordStatus;

import java.time.LocalDateTime;
import java.util.UUID;

@Component
public class ServiceCutMapper {

     public ServiceCut toDomain(CreateServiceCutRequest request, String organizationId, String createdBy) {
          return ServiceCut.builder()
                    .id(UUID.randomUUID().toString())
                    .organizationId(organizationId)
                    .recordStatus(RecordStatus.ACTIVE)
                    .createdAt(LocalDateTime.now())
                    .createdBy(createdBy)
                    .userId(request.getUserId())
                    .waterBoxId(request.getWaterBoxId())
                    .scheduledDate(request.getScheduledDate())
                    .cutReason(CutReason.valueOf(request.getCutReason()))
                    .debtAmount(request.getDebtAmount())
                    .reconnectionFeePaid(false)
                    .cutStatus(CutStatus.PENDING)
                    .notes(request.getNotes())
                    .build();
     }

     public ServiceCutResponse toResponse(ServiceCut serviceCut) {
          return ServiceCutResponse.builder()
                    .id(serviceCut.getId())
                    .organizationId(serviceCut.getOrganizationId())
                    .recordStatus(serviceCut.getRecordStatus().name())
                    .createdAt(serviceCut.getCreatedAt())
                    .createdBy(serviceCut.getCreatedBy())
                    .updatedAt(serviceCut.getUpdatedAt())
                    .updatedBy(serviceCut.getUpdatedBy())
                    .userId(serviceCut.getUserId())
                    .waterBoxId(serviceCut.getWaterBoxId())
                    .scheduledDate(serviceCut.getScheduledDate())
                    .executedDate(serviceCut.getExecutedDate())
                    .cutReason(serviceCut.getCutReason().name())
                    .debtAmount(serviceCut.getDebtAmount())
                    .reconnectionDate(serviceCut.getReconnectionDate())
                    .reconnectionFeePaid(serviceCut.getReconnectionFeePaid())
                    .cutStatus(serviceCut.getCutStatus().name())
                    .notes(serviceCut.getNotes())
                    .build();
     }
}
