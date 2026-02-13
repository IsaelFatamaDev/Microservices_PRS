package pe.edu.vallegrande.vgmsinfrastructure.domain.ports.in;

import pe.edu.vallegrande.vgmsinfrastructure.domain.models.WaterBoxTransfer;
import reactor.core.publisher.Mono;

public interface ITransferWaterBoxUseCase {
    Mono<WaterBoxTransfer> execute(String waterBoxId, String fromUserId, String toUserId, Double transferFee, String notes, String createdBy);
}
