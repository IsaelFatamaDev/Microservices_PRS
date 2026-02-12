package pe.edu.vallegrande.msdistribution.domain.ports.in.program;

import reactor.core.publisher.Mono;

public interface IRestoreDistributionProgramUseCase {
    Mono<Void> execute(String id, String restoredBy);
}
