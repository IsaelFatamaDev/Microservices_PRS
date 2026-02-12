package pe.edu.vallegrande.msdistribution.application.usecases.schedule;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pe.edu.vallegrande.msdistribution.domain.exceptions.specific.DistributionScheduleNotFoundException;
import pe.edu.vallegrande.msdistribution.domain.ports.in.schedule.IDeleteDistributionScheduleUseCase;
import pe.edu.vallegrande.msdistribution.domain.ports.out.schedule.IDistributionScheduleEventPublisher;
import pe.edu.vallegrande.msdistribution.domain.ports.out.schedule.IDistributionScheduleRepository;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class DeleteDistributionScheduleUseCaseImpl implements IDeleteDistributionScheduleUseCase {
    private final IDistributionScheduleRepository repository;
    private final IDistributionScheduleEventPublisher eventPublisher;

    @Override
    public Mono<Void> execute(String id, String deletedBy, String reason) {
        return repository.findById(id)
                .switchIfEmpty(Mono.error(new DistributionScheduleNotFoundException(id)))
                .flatMap(existing -> repository.update(existing.markAsDeleted(deletedBy)))
                .flatMap(saved -> eventPublisher.publishScheduleDeleted(id, deletedBy, reason))
                .then();
    }
}
