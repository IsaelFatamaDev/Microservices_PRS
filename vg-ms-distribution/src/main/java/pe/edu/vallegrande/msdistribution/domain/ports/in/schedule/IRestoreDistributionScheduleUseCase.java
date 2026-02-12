package pe.edu.vallegrande.msdistribution.domain.ports.in.schedule;

import reactor.core.publisher.Mono;

public interface IRestoreDistributionScheduleUseCase {
    Mono<Void> execute(String id, String restoredBy);
}
