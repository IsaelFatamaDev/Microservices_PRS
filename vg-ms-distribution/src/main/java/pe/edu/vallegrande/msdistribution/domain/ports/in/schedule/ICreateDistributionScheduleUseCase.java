package pe.edu.vallegrande.msdistribution.domain.ports.in.schedule;

import pe.edu.vallegrande.msdistribution.domain.models.DistributionSchedule;
import reactor.core.publisher.Mono;

public interface ICreateDistributionScheduleUseCase {
    Mono<DistributionSchedule> execute(DistributionSchedule schedule, String createdBy);
}
