package pe.edu.vallegrande.vgmsorganizations.domain.ports.in.street;

import pe.edu.vallegrande.vgmsorganizations.domain.models.Street;
import reactor.core.publisher.Mono;

public interface ICreateStreetUseCase {
    Mono<Street> execute(Street street, String createdBy);
}
