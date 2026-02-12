package pe.edu.vallegrande.msdistribution.domain.ports.in.program;

import reactor.core.publisher.Mono;

public interface IDeleteDistributionProgramUseCase {
    Mono<Void> execute(String id, String deletedBy, String reason);
}
