package pe.edu.vallegrande.vgmsorganizations.domain.ports.in.fare;

import pe.edu.vallegrande.vgmsorganizations.domain.models.Fare;
import reactor.core.publisher.Mono;

public interface IUpdateFareUseCase {
    Mono<Fare> execute(String id, Fare changes, String updatedBy);
}
