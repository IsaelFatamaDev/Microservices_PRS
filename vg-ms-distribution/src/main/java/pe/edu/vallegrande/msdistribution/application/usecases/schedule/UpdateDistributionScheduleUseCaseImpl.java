package pe.edu.vallegrande.msdistribution.application.usecases.schedule;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pe.edu.vallegrande.msdistribution.domain.exceptions.specific.DistributionScheduleNotFoundException;
import pe.edu.vallegrande.msdistribution.domain.models.DistributionSchedule;
import pe.edu.vallegrande.msdistribution.domain.ports.in.schedule.IUpdateDistributionScheduleUseCase;
import pe.edu.vallegrande.msdistribution.domain.ports.out.schedule.IDistributionScheduleEventPublisher;
import pe.edu.vallegrande.msdistribution.domain.ports.out.schedule.IDistributionScheduleRepository;
import reactor.core.publisher.Mono;

import java.util.HashMap;

@Service
@RequiredArgsConstructor
public class UpdateDistributionScheduleUseCaseImpl implements IUpdateDistributionScheduleUseCase {
    private final IDistributionScheduleRepository repository;
    private final IDistributionScheduleEventPublisher eventPublisher;

    @Override
    public Mono<DistributionSchedule> execute(String id, DistributionSchedule changes, String updatedBy) {
        return repository.findById(id)
                .switchIfEmpty(Mono.error(new DistributionScheduleNotFoundException(id)))
                .flatMap(existing -> {
                    DistributionSchedule updated = existing.updateWith(changes, updatedBy);
                    return repository.update(updated)
                            .flatMap(saved -> eventPublisher.publishScheduleUpdated(saved, new HashMap<>(), updatedBy).thenReturn(saved));
                });
    }
}
