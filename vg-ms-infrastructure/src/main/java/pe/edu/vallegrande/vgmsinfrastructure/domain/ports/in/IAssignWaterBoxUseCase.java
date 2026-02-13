package pe.edu.vallegrande.vgmsinfrastructure.domain.ports.in;

import pe.edu.vallegrande.vgmsinfrastructure.domain.models.WaterBoxAssignment;
import reactor.core.publisher.Mono;

public interface IAssignWaterBoxUseCase {
    Mono<WaterBoxAssignment> execute(String waterBoxId, String userId, String createdBy);
}
