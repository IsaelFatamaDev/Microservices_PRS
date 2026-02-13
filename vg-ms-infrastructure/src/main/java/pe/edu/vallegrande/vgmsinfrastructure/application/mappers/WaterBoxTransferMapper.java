package pe.edu.vallegrande.vgmsinfrastructure.application.mappers;

import org.springframework.stereotype.Component;
import pe.edu.vallegrande.vgmsinfrastructure.application.dto.response.WaterBoxTransferResponse;
import pe.edu.vallegrande.vgmsinfrastructure.domain.models.WaterBoxTransfer;

@Component
public class WaterBoxTransferMapper {

    public WaterBoxTransferResponse toResponse(WaterBoxTransfer domain) {
        return WaterBoxTransferResponse.builder()
                .id(domain.getId())
                .organizationId(domain.getOrganizationId())
                .waterBoxId(domain.getWaterBoxId())
                .fromUserId(domain.getFromUserId())
                .toUserId(domain.getToUserId())
                .transferDate(domain.getTransferDate())
                .transferFee(domain.getTransferFee())
                .notes(domain.getNotes())
                .recordStatus(domain.getRecordStatus())
                .createdAt(domain.getCreatedAt())
                .createdBy(domain.getCreatedBy())
                .build();
    }
}
