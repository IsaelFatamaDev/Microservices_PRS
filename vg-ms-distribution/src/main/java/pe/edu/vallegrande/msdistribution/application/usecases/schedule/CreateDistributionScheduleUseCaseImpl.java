package pe.edu.vallegrande.msdistribution.application.usecases.schedule;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pe.edu.vallegrande.msdistribution.domain.models.DistributionSchedule;
import pe.edu.vallegrande.msdistribution.domain.models.valueobjects.RecordStatus;
import pe.edu.vallegrande.msdistribution.domain.ports.in.schedule.ICreateDistributionScheduleUseCase;
import pe.edu.vallegrande.msdistribution.domain.ports.out.schedule.IDistributionScheduleEventPublisher;
import pe.edu.vallegrande.msdistribution.domain.ports.out.schedule.IDistributionScheduleRepository;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class CreateDistributionScheduleUseCaseImpl implements ICreateDistributionScheduleUseCase {
    private final IDistributionScheduleRepository repository;
    private final IDistributionScheduleEventPublisher eventPublisher;

    @Override
    public Mono<DistributionSchedule> execute(DistributionSchedule schedule, String createdBy) {
        LocalDateTime now = LocalDateTime.now();
        DistributionSchedule newSchedule = schedule.toBuilder()
                .recordStatus(RecordStatus.ACTIVE)
                .createdAt(now).createdBy(createdBy)
                .updatedAt(now).updatedBy(createdBy)
                .build();
        return repository.save(newSchedule)
                .flatMap(saved -> eventPublisher.publishScheduleCreated(saved, createdBy).thenReturn(saved));
    }
}
