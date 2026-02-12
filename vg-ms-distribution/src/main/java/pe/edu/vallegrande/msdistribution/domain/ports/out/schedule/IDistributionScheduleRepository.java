package pe.edu.vallegrande.msdistribution.domain.ports.out.schedule;

import pe.edu.vallegrande.msdistribution.domain.models.DistributionSchedule;
import pe.edu.vallegrande.msdistribution.domain.models.valueobjects.RecordStatus;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface IDistributionScheduleRepository {
    Mono<DistributionSchedule> save(DistributionSchedule schedule);
    Mono<DistributionSchedule> findById(String id);
    Mono<DistributionSchedule> update(DistributionSchedule schedule);
    Flux<DistributionSchedule> findAll();
    Flux<DistributionSchedule> findByRecordStatus(RecordStatus status);
    Flux<DistributionSchedule> findByOrganizationId(String organizationId);
    Mono<Void> deleteById(String id);
}
