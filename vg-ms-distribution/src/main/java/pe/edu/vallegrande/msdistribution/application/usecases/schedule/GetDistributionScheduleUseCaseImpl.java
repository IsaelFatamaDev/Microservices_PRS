package pe.edu.vallegrande.msdistribution.application.usecases.schedule;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pe.edu.vallegrande.msdistribution.domain.exceptions.specific.DistributionScheduleNotFoundException;
import pe.edu.vallegrande.msdistribution.domain.models.DistributionSchedule;
import pe.edu.vallegrande.msdistribution.domain.models.valueobjects.RecordStatus;
import pe.edu.vallegrande.msdistribution.domain.ports.in.schedule.IGetDistributionScheduleUseCase;
import pe.edu.vallegrande.msdistribution.domain.ports.out.schedule.IDistributionScheduleRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class GetDistributionScheduleUseCaseImpl implements IGetDistributionScheduleUseCase {
    private final IDistributionScheduleRepository repository;

    @Override
    public Mono<DistributionSchedule> findById(String id) {
        return repository.findById(id).switchIfEmpty(Mono.error(new DistributionScheduleNotFoundException(id)));
    }

    @Override
    public Flux<DistributionSchedule> findAll() { return repository.findAll(); }

    @Override
    public Flux<DistributionSchedule> findAllActive() { return repository.findByRecordStatus(RecordStatus.ACTIVE); }
}
