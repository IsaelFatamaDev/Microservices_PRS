package pe.edu.vallegrande.msdistribution.domain.ports.in.schedule;

import reactor.core.publisher.Mono;

public interface IDeleteDistributionScheduleUseCase {
    Mono<Void> execute(String id, String deletedBy, String reason);
}
