package pe.edu.vallegrande.vgmsorganizations.domain.ports.in.fare;

import pe.edu.vallegrande.vgmsorganizations.domain.models.Fare;
import reactor.core.publisher.Mono;

public interface IRestoreFareUseCase {
    Mono<Fare> execute(String id, String restoredBy);
}
