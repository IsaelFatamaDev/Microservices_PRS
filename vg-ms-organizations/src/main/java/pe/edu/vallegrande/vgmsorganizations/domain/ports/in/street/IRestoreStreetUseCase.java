package pe.edu.vallegrande.vgmsorganizations.domain.ports.in.street;

import pe.edu.vallegrande.vgmsorganizations.domain.models.Street;
import reactor.core.publisher.Mono;

public interface IRestoreStreetUseCase {
    Mono<Street> execute(String id, String restoredBy);
}
