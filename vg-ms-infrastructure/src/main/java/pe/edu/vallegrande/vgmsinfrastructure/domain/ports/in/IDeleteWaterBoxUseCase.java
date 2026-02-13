package pe.edu.vallegrande.vgmsinfrastructure.domain.ports.in;

import reactor.core.publisher.Mono;

public interface IDeleteWaterBoxUseCase {
    Mono<Void> execute(String id, String deletedBy);
}
