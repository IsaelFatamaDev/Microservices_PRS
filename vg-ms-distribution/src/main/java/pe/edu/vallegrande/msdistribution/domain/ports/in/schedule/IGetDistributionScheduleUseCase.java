package pe.edu.vallegrande.msdistribution.domain.ports.in.schedule;

import pe.edu.vallegrande.msdistribution.domain.models.DistributionSchedule;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface IGetDistributionScheduleUseCase {
    Mono<DistributionSchedule> findById(String id);
    Flux<DistributionSchedule> findAll();
    Flux<DistributionSchedule> findAllActive();
}
