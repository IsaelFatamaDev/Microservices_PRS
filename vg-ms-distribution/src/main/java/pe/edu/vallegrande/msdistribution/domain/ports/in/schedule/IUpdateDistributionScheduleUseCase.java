package pe.edu.vallegrande.msdistribution.domain.ports.in.schedule;

import pe.edu.vallegrande.msdistribution.domain.models.DistributionSchedule;
import reactor.core.publisher.Mono;

public interface IUpdateDistributionScheduleUseCase {
    Mono<DistributionSchedule> execute(String id, DistributionSchedule schedule, String updatedBy);
}
