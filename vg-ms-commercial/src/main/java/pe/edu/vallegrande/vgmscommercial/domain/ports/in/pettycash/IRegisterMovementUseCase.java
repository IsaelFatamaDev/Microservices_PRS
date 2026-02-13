package pe.edu.vallegrande.vgmscommercial.domain.ports.in.pettycash;

import pe.edu.vallegrande.vgmscommercial.domain.models.PettyCashMovement;
import reactor.core.publisher.Mono;

public interface IRegisterMovementUseCase {
    Mono<PettyCashMovement> execute(PettyCashMovement movement);
}
